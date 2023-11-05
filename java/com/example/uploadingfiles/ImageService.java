package com.example.uploadingfiles;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.uploadingfiles.storage.StorageException;
import com.example.uploadingfiles.storage.StorageFileNotFoundException;

@Service
public class ImageService {
	static int num=0;
    

    public ImageService() {
        
    }

    public Resource loadAsResource(String imageName) {
    	Path a= Paths.get("ppt").resolve(imageName);
		try {
			Resource resource = new UrlResource(a.toUri());
			return resource;
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + imageName, e);
		}
    }
    
    public Stream<Path> loadAll() {
    	Path rootLocation=Paths.get("ppt");
		try {
			return Files.walk(rootLocation, 1)
				.filter(path -> !path.equals(rootLocation))
				.map(rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}
	
	
	public static  List<String> imagestore(MultipartFile file) {
		int begin= file.getOriginalFilename().indexOf(".");
		int end= file.getOriginalFilename().length();
		String name=file.getOriginalFilename().substring(0, begin);
		String suffix=file.getOriginalFilename().substring(begin, end);
		List<String> imageUrls=null;
		try {
			if(suffix.endsWith(".ppt")) {
				imageUrls= extractSlidesFromPPT(file,name);
			}else if(suffix.endsWith(".pptx")) {
				imageUrls= extractSlidesFromPPTX(file,name);	 
			}else {
			}
		    
		} catch (IOException e) {
		      e.printStackTrace();
		}
		return imageUrls;
	}
	
	    // extract Slides From .ppt file to images
		public static List<String> extractSlidesFromPPT(MultipartFile file, String name) throws IOException {
			   List<String> imageUrls = new ArrayList<>(); 
			   InputStream inputStream =  new BufferedInputStream(file.getInputStream()); //transfer multipart file to fileinputstream
			   HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
			   Dimension pgsize = ppt.getPageSize();
			   int idx = 1;
			   for (HSLFSlide slide: ppt.getSlides()) {
				    BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB); // create img
				    Graphics2D graphics = img.createGraphics();   
				    slide.draw(graphics);   //render
				    num++;
				    String imageUrl= "ppt/"+num+name+"slide"+ idx + ".png"; //create img url
				    imageUrls.add("/ppt/"+num+ name + "slide"+ idx + ".png");   // add url to return var
				    FileOutputStream out = new FileOutputStream(imageUrl);  
				    javax.imageio.ImageIO.write(img, "png", out);    // put img into img url
				    out.close();
				    idx++;
			   }
			   return imageUrls;
		}
		
		// extract Slides From .pptx file to images
		public static List<String> extractSlidesFromPPTX(MultipartFile file, String name) throws IOException {
				    List<String> imageUrls = new ArrayList<>();
				    InputStream inputStream =  new BufferedInputStream(file.getInputStream());
				    XMLSlideShow ppt = new XMLSlideShow (inputStream);
					Dimension pgsize = ppt.getPageSize();
					int idx = 1;
					for (XSLFSlide slide: ppt.getSlides()) {
					     BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
					     Graphics2D graphics = img.createGraphics();
					     slide.draw(graphics);
					     num++;
					     String imageUrl= "ppt/" +num+ name +" slide "+ idx + ".png";
					     imageUrls.add("/ppt/" +num+name +" slide "+ idx + ".png");
					     FileOutputStream out = new FileOutputStream(imageUrl);
					     javax.imageio.ImageIO.write(img, "png", out);
					     out.close();
					     idx++;
					}
				    return imageUrls;
	    }
		
		public static void deleteimages()  {
			String path = "ppt/";
		    File file=new File(path);
		    if(!file.exists()) {
		    	return;
		    }
		    String[] imgs =file.list();
		    for(String name: imgs) {
		    	File temp=new File(path,name);
		    	temp.delete();
		    }
		    return ;
        }
}
