package com.sun.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * @Date 2020/2/7 21:17
 */
public class MyCHunkListener {

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        System.out.println(context.getStepContext().getStepName() + " chunk before...");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        System.out.println(context.getStepContext().getStepName() + " chunk after...");
    }
}
