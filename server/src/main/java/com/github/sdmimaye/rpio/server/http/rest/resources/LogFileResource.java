package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.common.log.LogInitializer;
import com.github.sdmimaye.rpio.server.http.rest.models.json.logs.JsonLogFile;
import com.google.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Path("/applogs")
public class LogFileResource {
    public static final String ERROR_HEADER = "X-Manager-Error-Key";
    private static final Logger logger = LoggerFactory.getLogger(LogFileResource.class);
    private final LogInitializer logInitializer;

    @Inject
    public LogFileResource(LogInitializer logInitializer) {
        this.logInitializer = logInitializer;
    }

    @GET
    @RolesAllowed("applicationLog-read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonLogFile> handleGet() {
        List<JsonLogFile> files = new ArrayList<>();
        File logFolder = logInitializer.getLogFolder();
        if (!logFolder.isDirectory())
            return files;

        List<File> logfiles = Arrays.asList(logInitializer.getLogFolder().listFiles(logInitializer.getLogFileFilter()));
        for (File logfile : logfiles) {
            files.add(new JsonLogFile(logfile));
        }

        Collections.reverse(files);
        return files;
    }

    @POST
    @Path("/selection/")
    @RolesAllowed("applicationLog-read")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public String handleGetSelection(List<String> filenames) throws IOException {
        cleanupTempFolder();

        File logFolder = logInitializer.getLogFolder();
        if (!logFolder.isDirectory())
            return null;

        File[] files = logFolder.listFiles((dir, name) -> filenames.contains(name));
        if(files == null)
            return null;

        List<File> selectedFilesList = Arrays.asList(files);
        InputStream stream = getZipInputStream(selectedFilesList);
        UUID identifier = UUID.randomUUID();
        File outputFile = new File(logInitializer.getTempFolder(), identifier.toString() + ".zip");
        FileOutputStream output = new FileOutputStream(outputFile.getAbsolutePath());
        IOUtils.copy(stream, output);
        stream.close();
        output.close();

        return identifier.toString();
    }

    @GET
    @Path("/temp/{identifier}")
    @RolesAllowed("applicationLog-read")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response handleGetTemp(@PathParam("identifier")String identifer) throws FileNotFoundException {
        try {
            UUID id = UUID.fromString(identifer);
            String logFileName = FilenameUtils.getName(FilenameUtils.normalize(id.toString() + ".zip"));
            File logFile = new File(logInitializer.getTempFolder(), logFileName);
            if (!logFile.exists() || !logFile.isFile() || !logFile.canRead())
                return Response.status(Response.Status.NOT_FOUND).build();

            try{
                InputStream stream = new FileInputStream(logFile);//stream will be closed by response handler
                LocalDateTime now = LocalDateTime.now();
                String filename = String.format("%s-selection.zip", now.toString());
                return Response.ok(stream, "application/zip").header("Content-Disposition", "attachment; filename=" + filename).build();
            }catch (IOException ioe){
                logger.error("Could not parse log-file: " + logFile.getAbsolutePath(), ioe);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }catch (RuntimeException re){
            throw re;
        }
        catch (Exception ex) {
            logger.warn("Could not parse uuid from string: " + identifer);
            return Response.status(Response.Status.BAD_REQUEST).header(ERROR_HEADER, "invalidUuid").build();
        }
    }

    @GET
    @Path("/all")
    @RolesAllowed("applicationLog-read")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response handleGetAll() {
        File logFolder = logInitializer.getLogFolder();
        if (!logFolder.isDirectory())
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        File[] logfiles = logInitializer.getLogFolder().listFiles(logInitializer.getLogFileFilter());
        InputStream stream = getZipInputStream(Arrays.asList(logfiles));
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("%s-all.zip", now.toString());
        return Response.ok(stream, "application/zip").header("Content-Disposition", "attachment; filename=" + filename).build();
    }

    private void cleanupTempFolder() {
        logger.warn("Temp-Folder will not be cleansed. Please delete old files after about an hour yourself!");
    }

    private InputStream getZipInputStream(final Iterable<File> files) {
        try {
            final PipedOutputStream out = new PipedOutputStream();
            new Thread(() -> {
                try {
                    ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(out));
                    for (File file : files) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipEntry.setTime(file.lastModified());
                        zipOutput.putNextEntry(zipEntry);

                        BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file));
                        try {
                            BufferedOutputStream fileOutput = new BufferedOutputStream(zipOutput);
                            IOUtils.copy(fileInput, fileOutput);
                            fileOutput.flush();
                        } finally {
                            fileInput.close();
                        }
                    }

                    zipOutput.close();
                } catch (IOException e) {
                    logger.info("Cancelling ZIP stream generation", e);
                } finally {
                    IOUtils.closeQuietly(out);
                }
            }, "Zip").start();

            return new PipedInputStream(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/file/{name}")
    @RolesAllowed("applicationLog-read")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response handleGetByName(@PathParam("name") String name) {
        String logFileName = FilenameUtils.getName(FilenameUtils.normalize(name));
        File logFile = new File(logInitializer.getLogFolder(), logFileName);
        if (!logFile.canRead()) {
            return Response.status(Response.Status.NOT_FOUND).header(ERROR_HEADER, "imageNotFound").build();
        }

        try{
            FileInputStream stream = new FileInputStream(logFile);//stream will be closed by response handler
            return Response.ok(stream, "text/plain").header("Content-Disposition", "attachment; filename=" + name).build();
        } catch (FileNotFoundException e) {
            logger.error("Could not find file: " + logFile.getAbsolutePath(), e);
            return Response.status(Response.Status.NOT_FOUND).header(ERROR_HEADER, e.getMessage()).build();
        }
    }
}
