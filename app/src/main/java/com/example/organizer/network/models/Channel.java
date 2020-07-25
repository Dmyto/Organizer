package com.example.organizer.network.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss", strict = false)
public class Channel {


    @ElementList(name = "item", inline = true)
    @Path("channel")
    public List<Post> posts = null;

    public List<Post> getPosts() {
        return posts;
    }

    @Element(name = "title")
    @Path("channel")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
