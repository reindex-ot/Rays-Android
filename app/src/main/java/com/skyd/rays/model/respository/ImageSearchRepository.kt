package com.skyd.rays.model.respository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.imageembedder.ImageEmbedder
import com.skyd.rays.appContext
import com.skyd.rays.base.BaseRepository
import com.skyd.rays.ext.toBitmap
import com.skyd.rays.model.bean.StickerWithTags
import com.skyd.rays.model.db.dao.sticker.StickerDao
import com.skyd.rays.model.db.objectbox.entity.StickerEmbedding
import com.skyd.rays.model.db.objectbox.entity.StickerEmbedding_
import com.skyd.rays.util.stickerUuidToUri
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.inValues
import io.objectbox.query.QueryBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ImageSearchRepository @Inject constructor(
    private val stickerDao: StickerDao,
    private val boxStore: BoxStore,
) : BaseRepository() {
    private var imageEmbedder: ImageEmbedder? = null

    init {
        setupImageEmbedder()
    }

    private fun setupImageEmbedder() {
        val baseOptionsBuilder = BaseOptions.builder()
        baseOptionsBuilder.setDelegate(Delegate.CPU)
        baseOptionsBuilder.setModelAssetPath("mobilenet_v3_small.tflite")
        try {
            val baseOptions = baseOptionsBuilder.build()
            val optionsBuilder = ImageEmbedder.ImageEmbedderOptions.builder()
                .setBaseOptions(baseOptions)
            val options = optionsBuilder.build()
            imageEmbedder = ImageEmbedder.createFromOptions(appContext, options)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Image embedder failed to load model with error: " + e.message)
        } catch (e: RuntimeException) {
            // This occurs if the model being used does not support GPU
            Log.e(TAG, "Image embedder failed to load model with error: " + e.message)
        }
    }

    fun imageSearch(base: Uri, maxResultCount: Int): Flow<List<StickerWithTags>> = flow {
        val nearestNeighborsResult = nearestNeighbors(base.toBitmap(), maxResultCount)
        emit(
            stickerDao.getAllStickerWithTagsList(nearestNeighborsResult.keys).sortedBy {
                nearestNeighborsResult[it.sticker.uuid]
            }
        )
    }.flowOn(Dispatchers.IO)

    fun nearestNeighbors(base: Bitmap, maxResultCount: Int): Map<String, Double> {
        val result = mutableMapOf<String, Double>()
        preprocessingEmbedding()
        imageEmbedder?.let { imageEmbedder ->
            val baseMpImage = BitmapImageBuilder(base).build()
            val baseEmbedding = imageEmbedder.embed(baseMpImage).embeddingResult()
                .embeddings()[0].floatEmbedding()
            val stickerEmbeddingBox = boxStore.boxFor(StickerEmbedding::class)
            val query = stickerEmbeddingBox
                .query(StickerEmbedding_.embedding.nearestNeighbors(baseEmbedding, maxResultCount))
                .build()
            result.putAll(query.findWithScores().associate { it.get().uuid to it.score })
        }
        return result
    }

    private fun preprocessingEmbedding() = imageEmbedder?.let { imageEmbedder ->
        val allStickers = stickerDao.getAllStickerUuidList()
        val stickerEmbeddingBox = boxStore.boxFor(StickerEmbedding::class)
        val cachedEmbeddings = stickerEmbeddingBox.query()
            .inValues(
                StickerEmbedding_.uuid,
                allStickers.toTypedArray(),
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .build()
            .find()
            .map { it.uuid }
            .toSet()
        val unCachedEmbeddings = allStickers.subtract(cachedEmbeddings)
        stickerEmbeddingBox.put(
            unCachedEmbeddings.map { unCachedUuid ->
                val image = BitmapImageBuilder(appContext, stickerUuidToUri(unCachedUuid)).build()
                StickerEmbedding(
                    uuid = unCachedUuid,
                    embedding = imageEmbedder.embed(image).embeddingResult()
                        .embeddings()[0].floatEmbedding()
                )
            }
        )
    }

    companion object {
        const val TAG = "ImageSearchRepository"
    }
}