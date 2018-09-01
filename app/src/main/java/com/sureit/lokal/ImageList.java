package com.sureit.lokal;

public class ImageList {
    private String imageUrl;
    private String postUrl;

    public ImageList(String imageUrl, String postUrl){
        this.imageUrl = imageUrl;
        this.postUrl = postUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }
}
