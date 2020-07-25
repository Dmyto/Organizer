package com.example.organizer.network.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "enclosure",strict = false)
public class ImageUrl {
    @Attribute(name = "url")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }
}
