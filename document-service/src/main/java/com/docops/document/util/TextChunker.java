package com.docops.document.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {

    private static final int MAX_CHUNK_SIZE = 800;

    public static List<String> chunk(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        String[] paragraphs = text.split("\\n\\n");

        StringBuilder buffer = new StringBuilder();

        for (String para : paragraphs) {
            if (buffer.length() + para.length() > MAX_CHUNK_SIZE) {
                chunks.add(buffer.toString().trim());
                buffer.setLength(0);
            }
            buffer.append(para).append("\n\n");
        }

        if (!buffer.isEmpty()) {
            chunks.add(buffer.toString().trim());
        }

        return chunks;
    }
}
