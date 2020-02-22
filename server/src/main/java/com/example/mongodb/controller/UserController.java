package com.example.mongodb.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import com.example.mongodb.dao.Likedao;
import com.example.mongodb.dao.Notificationdao;
import com.example.mongodb.dao.Screamdao;
import com.example.mongodb.dao.Userdao;
import com.example.mongodb.objects.Like;
import com.example.mongodb.objects.Notifications;
import com.example.mongodb.objects.Screams;
import com.example.mongodb.objects.User;
import com.example.mongodb.service.SequenceGeneratorService;
import com.fasterxml.jackson.core.JsonEncoding;


@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	Userdao userdao;

	@Autowired
	Likedao likedao;

	@Autowired
	Notificationdao notificationdao;

	@Autowired
	Screamdao screamdao;

	@Autowired
	SequenceGeneratorService seqGeneratorService;

	@PostMapping("/signup")
	public JSONObject create(@RequestBody User userModel) throws ParseException {
		userModel.setUserId(seqGeneratorService.generateSequence(User.SEQUENCE_NAME));
		User u1 = userdao.findByHandle(userModel.getHandle());
		JSONObject res = new JSONObject();
		JSONObject res1 = new JSONObject();
		if(u1 != null){
			res.put("handle","this handle is already taken");
			return res;
		}
		u1 = userdao.findByEmail(userModel.getEmail());
		if(u1 != null ){
			res.put("email","this email is already taken");
			return res;
		}
		userdao.save(userModel);
		// transform to json
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONObject json = (JSONObject) parser.parse("{token : " + userModel.getToken() + " }");
		return json;
	}
	
	@PostMapping("/login")
	public JSONObject login(@RequestBody User userModel) throws ParseException {
		String email = userModel.getEmail();
		String password = userModel.getPassword();
		List<User> a = userdao.findAll();
		for (int i = 0; i < a.size(); i++) {
			if (a.get(i).getEmail().equals(email) && a.get(i).getPassword().equals(password)) {
				System.out.println(a.get(i).getEmail());
				JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject json = (JSONObject) parser.parse("{token : " + a.get(i).getToken() + " }");
				return json;
			}
		}
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONObject json = (JSONObject) parser.parse("{general : Wrong credentials }");
		return json;
	}

	// to be checked later Parameter Issue
	@PostMapping("/user")
	public JSONObject update(@RequestBody JSONObject modifiedUser) throws ParseException {
		String handle = (String) modifiedUser.get("handle");
		String bio = (String) modifiedUser.get("bio");
		String location = (String) modifiedUser.get("location");
		String website = (String) modifiedUser.get("website");
		List<User> a = userdao.findAll();
		for (int i = 0; i < a.size(); i++) {
			if (a.get(i).getHandle().equals(handle)) {
				User b = a.get(i);
				if (bio != null)
					b.setBio(bio);
				if (location != null)
					b.setLocation(location);
				if (website != null)
					b.setWebsite(website);
				userdao.save(b);
				JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject json = (JSONObject) parser.parse("{message :Details added successfully}");
				return json;
			}
		}
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONObject json = (JSONObject) parser.parse("{error :error adding details}");
		return json;
	}
	
	@GetMapping("/user")
	public JSONObject getAuthenticatedUser(@RequestBody JSONObject userData) throws ParseException {
		String handle = (String) userData.get("handle");
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);

		JSONObject parsLike = new JSONObject();
		JSONArray likesarray = new JSONArray();
		List<Like> likes =  likedao.findByUserHandle(handle);
		JSONObject json = getUserdata(handle);
		String likesString;

		for(int i=0; i<likes.size();i++){
		
		likesString="{userHandle :"+likes.get(i).getUserHandle() + ", screamId : "+likes.get(i).getScreamId()+"}";
		parsLike = (JSONObject) parser.parse(likesString);
		likesarray.appendElement(parsLike);
		
		}
		JSONObject jall = new JSONObject();
		jall.put("credentials", json);
		jall.put("likes",likesarray);
		//Notifications
		List<Notifications> nots = notificationdao.findByRecipient(handle);
		nots.sort(new Comparator<Notifications>() {
			@Override
			public int compare(Notifications o1, Notifications o2) {
				if(o1.getCreatedAt().compareTo(o2.getCreatedAt()) > 0){
					return 1;
				}else
					return -1;
			}
			
		});

		String notsString;
		JSONObject parseNots = new JSONObject();
		JSONArray notsArray = new JSONArray();
		for(int i=0;i<10 && i <nots.size();i++){
				notsString ="{recipient: "+nots.get(i).getRecipient()+",sender: "+nots.get(i).getSender()+",createdAt : "+nots.get(i).getCreatedAt().toString()+",screamId : "+nots.get(i).getScreamId()+",type: "+nots.get(i).getType()+","+
				"read:" +nots.get(i).getRead()+","+
				"notificationId: "+nots.get(i).getNotificationId()+"}";
		parseNots = (JSONObject) parser.parse(notsString);
		notsArray.appendElement(parseNots);
		}		
		jall.put("notifications",notsArray);
		return jall;

	}

	@GetMapping(value = "/user/{handle}")
	public JSONObject getHandleData(@PathVariable(name = "handle",required = true) String handle)
			throws ParseException {
		JSONObject json = getUserdata(handle);
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONObject jall = new JSONObject();

		List<Screams> screams = screamdao.findByUserHandle(handle);
		JSONObject parseScream = new JSONObject();
		JSONArray screamArray = new JSONArray();
		String screamsString;
		for(int i=0;i<screams.size();i++){
			screamsString ="{body: "+screams.get(i).getBody()+
			",userHandle: "+screams.get(i).getUserHandle()+
			",createdAt : "+screams.get(i).getCreatedAt().toString()+
			",userImage : "+screams.get(i).getUserImage()+
			",likeCount: "+screams.get(i).getLikeCount()+","+
			"commentCount:" +screams.get(i).getCommentCount()+","+
			"screamId: "+screams.get(i).getScreamId()+"}";
			parseScream = (JSONObject) parser.parse(screamsString);
			screamArray.appendElement(parseScream);
		}
		jall.put("user",json);
		jall.put("screams",screamArray);

		return jall;
	}

	@PostMapping("/notifications")
	public JSONObject markNotificationsRead(@RequestBody List<ObjectId> notificationsIds ){
		JSONObject j = new JSONObject();
		Notifications not;
		
		try{
		for(int i=0;i<notificationsIds.size();i++){
			not = notificationdao.findByNotificationsId(notificationsIds.get(i));
			not.setRead(true);
			notificationdao.save(not);
		}
		j.put("message","Notifications Marked Read");
	}catch(Exception e){
		j.put("errors","Can't update notification");
	}
		return j;
		
	}



	public JSONObject getUserdata(String handle){
		JSONObject json = new JSONObject();

		
		User user = userdao.findByHandle(handle);
		json.put("createdAt", user.getCreatedAt());
		json.put("location", user.getLocation());
		json.put("website", user.getWebsite());
		json.put("bio", user.getBio());
		json.put("handle", user.getHandle());
		json.put("userId", user.getUserId());
		json.put("email", user.getEmail());
		json.put("imageUrl", user.getImageUrl());

		return json;
	}

	
}
	
		


