package wso2.org.jax.webservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;

@Path("/")
public class FileService {
    private static final Log log = LogFactory.getLog(FileService.class);
    //todo: add relative path to save files
    private static final String fileUploadPath = "/home/thilini/Documents/Wso2_Packs/wso2as-5.3.0/repository/deployment/server/jaggeryapps/geo-map/images/";
    private static final String fileSavedPath = "/home/thilini/Documents/Wso2_Packs/wso2as-5.3.0/repository/deployment/server/jaggeryapps/geo-map/indoor_maps/";


    @POST
    @Path("/uploadImageFile")//todo: handle empty input
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //Save the selected image file in the fileUploadPath
    public Response uploadFile(List<Attachment> attachments,
                               @Context HttpServletRequest request) {
        MultivaluedMap<String, String> map = null;
        for (Attachment attachment : attachments) {
            DataHandler handler = attachment.getDataHandler();
            try {
                InputStream stream = handler.getInputStream();
                map = attachment.getHeaders();
                OutputStream out = new FileOutputStream(new File(fileUploadPath
                        + getImageName(map)));

                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = stream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                stream.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                log.error("Exception thrown when uploading image file: " + e.getMessage(), e);
            }
        }
        return Response.ok(getImageName(map)).build();
    }

    @POST
    @Path("/deleteImageFile")
    @Consumes(MediaType.TEXT_PLAIN)
    //Delete the selected image file from fileUploadPath
    public Response deleteImageFile(String relativeFilePath) {
        String filePath = fileUploadPath + relativeFilePath.split("/")[1];
        File file = new File(filePath);
        boolean isFileDeleted = file.delete();
        if (isFileDeleted) {
            return Response.ok("file deleted successfully").build();
        } else {
            return Response.ok("delete operation failed!!").build();
        }
    }

    @POST
    @Path("/saveIndoorMap")
    @Consumes(MediaType.TEXT_PLAIN)
    //save indoor map in the fileSavedPath
    public Response saveIndoorMap(String jsonString) {
        String[] requestParameters = jsonString.split("&");
        String indoorMapName = requestParameters[0].split("=")[1];
        String indoorMapContent = requestParameters[1].split("=")[1];

        //write to file
        try {
            FileWriter fileWriter = new FileWriter(new File(fileSavedPath + indoorMapName + ".json"));
            fileWriter.write(indoorMapContent);
            fileWriter.close();
        } catch (IOException e) {
            log.error("Exception thrown when saving map: " + e.getMessage(), e);
        }
        return Response.ok("file saved successfully").build();
    }

    /*@GET
    @Path("/showAllIndoorMaps")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllSavedFileNames(String fileName){
        return Response.ok();//todo return indoor map obj name array
    }*/

    private String getImageName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition")
                .split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String exactFileName = name[1].trim().replaceAll("\"", "");
                return exactFileName;
            }
        }
        return "unknown";//todo chage return string
    }
}