package com.telegrambot.telegramcatbot.session;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatCreationSession {
    private String name;
    private String description;
    private String photoFileId;
}

