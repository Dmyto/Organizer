package com.example.organizer.network.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class Post {
    @Element(name = "title")
    private String title;
    @Element(name = "description")
    private String description;
    @Element(name = "pubDate")
    private String pubDate;
    @Element(name = "link")
    private String link;

    @Element(name = "enclosure")
    private ImageUrl imageUrl;

    public ImageUrl getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getLink() {
        return link;
    }
}
