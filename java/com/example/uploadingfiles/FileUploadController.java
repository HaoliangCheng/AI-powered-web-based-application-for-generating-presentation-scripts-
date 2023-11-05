package com.example.uploadingfiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.storage.FileSystemStorageService;
import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

import com.example.uploadingfiles.database.SlidesToScript;
import com.example.uploadingfiles.database.User;
import com.example.uploadingfiles.service.ConvertService;
import com.example.uploadingfiles.service.MyUserDetailService;

@Controller
public class FileUploadController {
	
	private int maximumUploadingNum = 15;
	private final StorageService storageService;
	private final ConvertService convertService;
	private final MyUserDetailService myUserDetailService;
	private ImageService imageService;
	private List<String> imageList = new ArrayList<>();
	

	@Autowired
	public FileUploadController(StorageService storageService, ConvertService convertService,
			MyUserDetailService myUserDetailService, ImageService imageService) {
		this.storageService = storageService;
		this.convertService = convertService;
		this.myUserDetailService = myUserDetailService;
		this.imageService = imageService;;
	}
	

	@GetMapping("/uploadForm")
	public String uploadForm() {

		return "uploadForm";
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		return "uploadForm"; 
	}

	@GetMapping("/uploadPage")
	public String homePage(Model model) {
		String username = getUsername();
		model.addAttribute("username", username);
		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
								.build().toUri().toString())
						.collect(Collectors.toList()));
		model.addAttribute("imageUrls", imageList);
		return "uploadPage";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	
	@GetMapping("/ppt/{imageName}")
	public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
	    Resource image = imageService.loadAsResource(imageName);
	    if (image.exists() && image.isReadable()) {
            return ResponseEntity.ok().body(image);
        } else {
             return ResponseEntity.notFound().build();
        }       
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
		
		String userName = getUsername();		
		User user = myUserDetailService.findByUsername(userName);
		int entityNum = myUserDetailService.countUserEntities(user);

		if (entityNum >= maximumUploadingNum) {
			redirectAttributes.addFlashAttribute("message", "You have reached the maximum uploading number.");
			return "redirect:/uploadPage#document";
		}
		
		// create project folder
		String userNameNum = userName + "_" + entityNum;
		Path userCacheFolder = FileSystemStorageService.userCacheGetter().resolve(userNameNum);
		try {
			Files.createDirectories(userCacheFolder);
			System.out.println("Base folder created: " + userCacheFolder);
		}catch (IOException e) {
			System.err.println("Failed to create user folder");
			e.printStackTrace();
		}
		
		SlidesToScript slidesToScriptEntity = new SlidesToScript();
        //	PDF file original name
		slidesToScriptEntity.setPdfName(file.getOriginalFilename());
		
		Path[] paths = storageService.store(file);
		String originalSlidesPath = paths[0].toString();
		String scriptPath = "src/main/resources/pythonCodes/parse_pptx.py";
		String chatGPTScript = "src/main/resources/pythonCodes/python_chatgpt.py";
		Path userDirectPath = paths[1];
		
		Path userResPath = userDirectPath.resolve(userNameNum);
		try {
			Files.createDirectories(userResPath);
			System.out.println("Base folder in user_dir created: " + userResPath);
		}catch (IOException e) {
			System.err.println("Failed to create user folder");
			e.printStackTrace();
		}
		String finaldestPath = userResPath.toString();
		String transitionalPath = userCacheFolder.toString();
		
		// convert pptx to json file 
		System.out.println(PySlidesToText.runPythonScript(scriptPath, originalSlidesPath, transitionalPath));
		// convert json file to text file
		System.out.println(PySlidesToText.runPythonScript(chatGPTScript, transitionalPath, finaldestPath));
		
		slidesToScriptEntity.setPdfAddr(originalSlidesPath);
		slidesToScriptEntity.setChatGptAddr(finaldestPath);
		
		convertService.createEntity(user.getId(), slidesToScriptEntity);
		
		
		imageList = ImageService.imagestore(file);
		String filePath = finaldestPath+"/content.txt";
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                List<String> lines = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
        	    lines.add(line);
                }
		reader.close();
                redirectAttributes.addFlashAttribute("contents", lines);
		redirectAttributes.addFlashAttribute("size", imageList.size());
		
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/uploadPage#document";

	}
	
	public String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			return currentUserName;
		}
		return null; // Or throw an exception, or a custom message
	}
	
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
