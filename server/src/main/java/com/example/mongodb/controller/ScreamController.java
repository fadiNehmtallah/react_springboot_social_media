package com.example.mongodb.controller;

import java.util.List;

import com.example.mongodb.dao.Commentdao;
import com.example.mongodb.dao.Likedao;
import com.example.mongodb.dao.Notificationdao;
import com.example.mongodb.dao.Screamdao;
import com.example.mongodb.dao.Userdao;
import com.example.mongodb.objects.Comment;
import com.example.mongodb.objects.Like;
import com.example.mongodb.objects.Screams;
import com.example.mongodb.objects.User;
import com.example.mongodb.service.SequenceGeneratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RestController
@RequestMapping("/api")
public class ScreamController {
    @Autowired
    Userdao userdao;

    @Autowired
    Likedao likedao;

    @Autowired
    Notificationdao notificationdao;

    @Autowired
    Screamdao screamdao;

    @Autowired
    Commentdao commentdao;

    @Autowired
    SequenceGeneratorService seqGeneratorService;

    @GetMapping("/screams")
    public JSONArray getAllScreams() throws ParseException {
        List<Screams> scream=screamdao.findAll();
        JSONObject screamJson = new JSONObject();
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray screamArray = new JSONArray();
        String screamString;
        for(int i=0;i<scream.size();i++){
            screamString = "{screamId : "+scream.get(i).getScreamId()+","+
            "body : "+scream.get(i).getBody()+","+
            "userHandle : "+scream.get(i).getUserHandle()+","+
            "createdAt : "+scream.get(i).getCreatedAt()+","+
            "userImage : "+scream.get(i).getUserImage()+","+
            "likeCount : "+scream.get(i).getLikeCount()+","+
            "commentCount : "+scream.get(i).getCommentCount()+"}";
            screamJson = (JSONObject) parser.parse(screamString);
            screamArray.appendElement(screamJson);
        }
        return screamArray;
    }
    @PostMapping("/scream")
    public JSONObject addScream(@RequestBody Screams scream) throws ParseException {
        scream.setScreamId(seqGeneratorService.generateSequence(Screams.SEQUENCE_NAME));
        screamdao.save(scream);
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        String screamString = "{screamId : "+scream.getScreamId()+","+
        "body : "+scream.getBody()+","+
        "userHandle : "+scream.getUserHandle()+","+
        "createdAt : "+scream.getCreatedAt()+","+
        "userImage : "+scream.getUserImage()+","+
        "likeCount : "+scream.getLikeCount()+","+
        "commentCount : "+scream.getCommentCount()+"}";
        JSONObject screamJson = (JSONObject) parser.parse(screamString);
        return screamJson;
    }

    @GetMapping(value = "/scream/{screamId}")
    public JSONObject getScream(@PathVariable(name = "screamId",required = true) long screamId) throws ParseException {
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject screamJson = getScreamdata(screamId);
        List<Comment> comments = commentdao.findByScreamId(screamId);
        JSONArray commentsArray = new JSONArray();
        String commentsString ;
        JSONObject commentJson = new JSONObject();
        for(int i=0;i<comments.size();i++){
            commentsString = "{body :"+comments.get(i).getBody()+","+
            "createdAt :"+comments.get(i).getCreatedAt()+","+
            "userHandle :"+comments.get(i).getUserHandle()+","+
            "userImage :"+comments.get(i).getUserImage()+","+
            "screamId :"+comments.get(i).getScreamId()+",}";
            commentJson = (JSONObject) parser.parse(commentsString);
            commentsArray.appendElement(commentJson);
        }
        screamJson.put("comments",commentsArray);
        return screamJson;
        
    }
    @DeleteMapping(value ="/scream/{screamId}")
    public JSONObject deleteScream(@PathVariable(name ="screamId",required = true) long screamId){
        JSONObject res = new JSONObject();
        try{
            screamdao.deleteByScreamId(screamId);
            res.put("message","Scream deled Successfully");
            return res;
        }catch(Exception e){
            res.put("error",e);
            return res;
        }
    }
    @GetMapping(value ="/scream/{screamId}/unlike")
    public JSONObject unlikeScream(@RequestBody JSONObject user,
    @PathVariable(name = "screamId",required = true) long screamId)
    throws ParseException {
        Screams scream = screamdao.findByScreamId(screamId);
        JSONObject res = new JSONObject();
        String userHandle = (String) user.get("userHandle");
        System.out.println(screamId+"  "+userHandle);
        Like like = likedao.findByScreamIdAndUserHandle(screamId,userHandle);
        
        if( like == null){
            res.put("error","Scream Not liked");
            return res;
        }else{
            likedao.delete(like);
            scream.setLikeCount(scream.getLikeCount()-1);
            screamdao.save(scream);
            res = getScreamdata(screamId);
            return res;
        }
        
    }

    @GetMapping(value ="/scream/{screamId}/like")
    public JSONObject likeScream(@RequestBody JSONObject user,
    @PathVariable(name = "screamId",required = true) long screamId)
    throws ParseException {
        Screams scream = screamdao.findByScreamId(screamId);
        JSONObject res = new JSONObject();
        String userHandle = (String) user.get("userHandle");
        Like like = likedao.findByScreamIdAndUserHandle(screamId,userHandle);
        
        if( like != null){
            res.put("error","Scream already liked");
            return res;
        }else{
            like = new Like(userHandle,screamId);
            likedao.save(like);
            scream.setLikeCount(scream.getLikeCount()+1);
            screamdao.save(scream);
            res = getScreamdata(screamId);
            return res;
        }
        
    }
    @PostMapping(value = "/scream/{screamId}/comment")
    public JSONObject commentScream(@RequestBody JSONObject user,@PathVariable(name = "screamId",required = true) long screamId)
            throws ParseException {
        Screams scream = screamdao.findByScreamId(screamId);
        String userHandle = (String) user.get("userHandle");
        User us = userdao.findByHandle(userHandle);
        String userImage = us.getImageUrl();
        String body =(String) user.get("body");
        Comment cmnt = new Comment(screamId,userHandle,userImage,body);
        commentdao.save(cmnt);
        scream.setCommentCount(scream.getCommentCount()+1);
        screamdao.save(scream);
        String comment = "{body :"+cmnt.getBody()+","+
        "createdAt :"+cmnt.getCreatedAt()+","+
        "screamId :"+cmnt.getScreamId()+","+
        "userHandle :"+cmnt.getUserHandle()+","+
        "userImage :"+cmnt.getUserImage()+"}";
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject jsonObject = new JSONObject();
        jsonObject = (JSONObject) parser.parse(comment);
        return jsonObject;
    }
    

    public JSONObject getScreamdata(long screamId) throws ParseException {
        JSONObject screamJson = new JSONObject();
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        Screams scream = screamdao.findByScreamId(screamId);
        String screamString = "{screamId : "+scream.getScreamId()+","+
        "body : "+scream.getBody()+","+
        "userHandle : "+scream.getUserHandle()+","+
        "createdAt : "+scream.getCreatedAt()+","+
        "userImage : "+scream.getUserImage()+","+
        "likeCount : "+scream.getLikeCount()+","+
        "commentCount : "+scream.getCommentCount()+"}";
         screamJson = (JSONObject) parser.parse(screamString);
        return screamJson;
    }

}