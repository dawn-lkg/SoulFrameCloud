package com.clm.modules.file.utils;

import cn.hutool.core.io.FileUtil;
import com.clm.common.core.utils.CommonUtils;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import static com.google.common.io.Files.getFileExtension;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Slf4j
@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class FileUtils {

    private final MinioClient minioClient;

    /**
     * 获取视频第一帧并作为缩略图上传到 MinIO，返回链接
     *
     * @param video      视频文件
     * @param fileName   原始文件名（用于生成缩略图名）
     * @param bucketName MinIO桶名
     * @return 缩略图访问链接（带签名），失败返回null
     */
    public String getVideoFirstFrameThumbnailUrl(MultipartFile video, String fileName, String bucketName) {
        ByteArrayOutputStream baos = null;
        File tempFile = null;
        try {
            // 获取第一帧图片
            tempFile = multipartToFileForVideo(video);
            
            Picture frame = null;
            
            // 使用 JCodec 的标准方法获取第一帧
            // 尝试获取第0帧（第一帧）
            frame = FrameGrab.getFrameFromFile(tempFile, 0);
            
            // 如果获取第0帧失败，尝试获取其他帧
            if (frame == null) {
                log.warn("获取第0帧失败，尝试获取其他帧");
                // 尝试获取第1-10帧
                for (int i = 1; i <= 10 && frame == null; i++) {
                    try {
                        frame = FrameGrab.getFrameFromFile(tempFile, i);
                    } catch (Exception e) {
                        log.debug("获取第{}帧失败: {}", i, e.getMessage());
                    }
                }
            }
            
            if (frame == null) {
                log.warn("未提取到视频第一帧");
                return null;
            }
            BufferedImage image = AWTUtil.toBufferedImage(frame);

            // 写入为JPEG到字节流
            baos = new ByteArrayOutputStream();

            // 构造缩略图文件名
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String thumbnailFileName = "thumb_" + fileName + ".jpg";
            String thumbnailObjectName = datePath + "/thumbnail/" + thumbnailFileName;
            
            // 生成缩略图 - 保持宽高比
            int thumbWidth = 200;
            int thumbHeight = 200;
            
            // 计算缩放比例，保持宽高比
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            double widthRatio = (double) thumbWidth / originalWidth;
            double heightRatio = (double) thumbHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);
            
            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);
            
            // 创建缩略图
            BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbImage.createGraphics();
            
            // 设置高质量缩放
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制缩略图（居中绘制，填充整个背景）
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, thumbWidth, thumbHeight);
            // 将原图绘制到计算出的新尺寸，居中显示
            int x = (thumbWidth - newWidth) / 2;
            int y = (thumbHeight - newHeight) / 2;
            g2d.drawImage(image, x, y, newWidth, newHeight, null);
            g2d.dispose();
            
            // 将缩略图写入到字节流（直接写入 baos）
            ImageIO.write(thumbImage, "jpg", baos);

            // 上传到MinIO
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(thumbnailObjectName)
                    .stream(new ByteArrayInputStream(baos.toByteArray()), baos.size(), -1)
                    .contentType("image/jpeg")
                    .build()
            );
            String thumbnailUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(thumbnailObjectName)
                    .build()
            );
            return thumbnailUrl.substring(0, thumbnailUrl.indexOf("?"));
        } catch (Exception e) {
            log.error("获取视频第一帧缩略图失败: {}", e.getMessage(), e);
            return null;
        } finally {
            if (baos != null) {
                try { baos.close(); } catch (IOException ignore) {}
            }
            // 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * MultipartFile 转为临时文件，仅用于视频处理防止与原有方法冲突
     *
     * @param multipart MultipartFile
     * @return File
     * @throws IOException
     */
    private File multipartToFileForVideo(MultipartFile multipart) throws IOException {
        String originalFileName = multipart.getOriginalFilename();
        String suffix = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        File tempFile = File.createTempFile("video_", suffix);
        multipart.transferTo(tempFile);
        tempFile.deleteOnExit();
        return tempFile;
    }

    /**
     * 生成缩略图
     *
     * @param file 原文件
     * @param fileName 原文件名
     * @param bucketName 存储桶
     * @return 缩略图URL
     */
    public String getImageThumbnailUrl(MultipartFile file, String fileName, String bucketName) {
        try {
            // 读取原图
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                return null;
            }

            // 缩略图尺寸
            int targetWidth = 200;
            int targetHeight = 200;

            // 计算缩放比例，保持宽高比
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            double widthRatio = (double) targetWidth / originalWidth;
            double heightRatio = (double) targetHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);

            // 创建缩略图
            BufferedImage thumbnail = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            // 获取图片格式
            String extension = FileUtil.extName(fileName).toLowerCase(Locale.ROOT);
            String thumbnailFormat = Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension) ? extension : "jpg";

            // 写入字节数组（直接写入 baos）
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, thumbnailFormat, baos);
            baos.flush();

            // 上传到MinIO
            String thumbnailFileName = "thumb_" + fileName;
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String thumbnailObjectName = datePath + "/thumbnail/" + thumbnailFileName;

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(thumbnailObjectName)
                            .stream(bais, baos.size(), -1)
                            .contentType("image/" + thumbnailFormat)
                            .build()
            );

            // 获取缩略图URL（不带过期时间）
            String presignedObjectUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(thumbnailObjectName)
                            .build()
            );
            return presignedObjectUrl.substring(0, presignedObjectUrl.indexOf("?"));
        } catch (Exception e) {
            log.warn("生成缩略图失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * MultipartFile转File
     */
    public static File multipartToFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 创建临时文件
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        File tempFile = null;

        try {
            File.createTempFile("upload_", "." + extension);
            // 转换文件
            multipartFile.transferTo(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("创建临时文件失败", e);
        }


        // 设置临时文件在JVM退出时自动删除
        tempFile.deleteOnExit();

        return tempFile;
    }
}
