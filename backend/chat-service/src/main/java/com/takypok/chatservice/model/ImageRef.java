package com.takypok.chatservice.model;

/** An image reference extracted from a retrieved knowledge-base chunk (markdown `![alt](url)`). */
public record ImageRef(String url, String alt) {}
