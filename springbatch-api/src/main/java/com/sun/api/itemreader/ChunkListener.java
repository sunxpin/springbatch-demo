package com.sun.api.itemreader;

import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * chunk监听器
 *
 * @Date 2020/2/8 12:53
 */

public class ChunkListener implements org.springframework.batch.core.ChunkListener {
    @Override
    public void beforeChunk(ChunkContext chunkContext) {
        System.out.println(chunkContext.getStepContext().getStepName() + " before...");
    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
        System.out.println(chunkContext.getStepContext().getStepName() + " after...");

    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {
        System.out.println(chunkContext.getStepContext().getStepName() + " error...");

    }
}
