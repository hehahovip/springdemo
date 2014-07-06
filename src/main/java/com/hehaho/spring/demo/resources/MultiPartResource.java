/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.hehaho.spring.demo.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpRequest;

/**
 * @author Michal Gajdos (michal.gajdos at oracle.com)
 */
@Path("/form")
public class MultiPartResource {
	
	private static Logger log = Logger.getLogger(MultiPartResource.class.toString());

	private File fileUploadPath = new File("C://uploaded/");
	
/*    @POST
    @Path("part")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@FormDataParam("part") String s) {
        return s;
    }

    @POST
    @Path("part-file-name")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(
            @FormDataParam("part") String s,
            @FormDataParam("part") FormDataContentDisposition d) {
        return s + ":" + d.getFileName();
    }*/

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("files[]") InputStream uploadedInputStream,
		@FormDataParam("files[]") FormDataContentDisposition fileDetail,
		@Context UriInfo ui) {
 
		String filename = fileDetail.getFileName();
		try {
			log.info("Uploaded file, GBK: " + new String(fileDetail.getFileName().getBytes("iso8859-1"), "UTF-8"));
			filename = new String(fileDetail.getFileName().getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String uploadedFileLocation = "C://uploaded/" + filename;
 
		log.info("Uploaded file: " + fileDetail.getFileName());
		
		writeToFile(uploadedInputStream, uploadedFileLocation);
		
		JSONArray json = new JSONArray();
	    JSONObject jsono = new JSONObject();
	    jsono.put("name", filename);
	    jsono.put("size", fileDetail.getSize());
	    jsono.put("url", "upload?getfile=" + filename);
	    jsono.put("thumbnail_url", "upload?getthumb=" + filename);
	    jsono.put("delete_url", "upload?delfile=" + filename);
	    jsono.put("delete_type", "GET");
	    json.put(jsono);
 
		return Response.status(200).entity(json.toString()).build();
	}
	
	@POST
	@Path("/upload1")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(
		@FormDataParam("files[]") InputStream uploadedInputStream,
		@FormDataParam("files[]") FormDataContentDisposition fileDetail,
		@Context HttpServletRequest request) {
 
		if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }

        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        JSONArray json = new JSONArray();
        try {
            List<FileItem> items = uploadHandler.parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                        File file = new File(fileUploadPath, item.getName());
                        item.write(file);
                        JSONObject jsono = new JSONObject();
                        jsono.put("name", item.getName());
                        jsono.put("size", item.getSize());
                        jsono.put("url", "upload?getfile=" + item.getName());
                        jsono.put("thumbnail_url", "upload?getthumb=" + item.getName());
                        jsono.put("delete_url", "upload?delfile=" + item.getName());
                        jsono.put("delete_type", "GET");
                        json.put(jsono);
                }
            }
        } catch (FileUploadException e) {
                throw new RuntimeException(e);
        } catch (Exception e) {
                throw new RuntimeException(e);
        } finally {
        }

 
		return Response.status(200).entity(json.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	@POST
	@Path("/upload2")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile2(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {
 
		String uploadedFileLocation = "C://uploaded/" + fileDetail.getFileName();
 
		log.info("Uploaded file: " + fileDetail.getFileName());
		String filename = fileDetail.getFileName();
		try {
			log.info("Uploaded file, GBK: " + new String(fileDetail.getFileName().getBytes("iso8859-1"), "UTF-8"));
			filename = new String(fileDetail.getFileName().getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writeToFile(uploadedInputStream, uploadedFileLocation);
		
		JSONArray json = new JSONArray();
	    JSONObject jsono = new JSONObject();
	    jsono.put("name", filename);
	    jsono.put("size", fileDetail.getSize());
	    jsono.put("url", "upload?getfile=" + filename);
	    jsono.put("thumbnail_url", "upload?getthumb=" + filename);
	    jsono.put("delete_url", "upload?delfile=" + filename);
	    jsono.put("delete_type", "GET");
	    json.put(jsono);
 
		return Response.status(200).entity(json.toString()).build();
	}
	
	
	// save uploaded file to new location
		private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {
			OutputStream out = null;
			try {
				out = new FileOutputStream(new File(
						uploadedFileLocation));
				int read = 0;
				byte[] bytes = new byte[1024];
	 
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				
			} catch (IOException e) {
	 
				e.printStackTrace();
			} finally{
				if(uploadedInputStream != null){
					try {
						uploadedInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
	 
		}
	
}
