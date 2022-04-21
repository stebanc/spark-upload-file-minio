package dev.stebanc.minio;

import static spark.Spark.*;

import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {

        String accessKey = "admin";
        String secretKey = "password";

        MinioClient minioClient = MinioClient.builder().endpoint("http://127.0.0.1:9000")
                .credentials(accessKey, secretKey).build();

        try {

            minioClient.listBuckets().forEach(b -> System.out.println(b.name()));
        } catch (Exception e) {
            // TODO: handle exception
        }

        staticFiles.location("/public");

        port(8080);

        post("/api/upload", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
            Part filePart = req.raw().getPart("myfile");

            try (InputStream inputStream = filePart.getInputStream()) {
                // OutputStream outputStream = new FileOutputStream(
                // "/home/ecanaza/Documentos/tmp/" + filePart.getSubmittedFileName());
                // IOUtils.copy(inputStream, outputStream);
                // outputStream.close();

                /*
                 * UploadObjectArgs.Builder builder = UploadObjectArgs.builder().bucket("cats")
                 * .object("cat.jpg").filename(tempFile.toString());
                 */

                // minioClient.uploadObject(builder.build());
                try {

                    minioClient.putObject(PutObjectArgs.builder().bucket("cats")
                            .object(UUID.randomUUID().toString() + filePart.getSubmittedFileName())
                            // .stream(inputStream, 0, -1).build());
                            .stream(inputStream, -1, 10485760).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                inputStream.close();
                filePart.delete();
            }

            return "File uploaded and saved." + "sdfsdf";
        });
    }
}
