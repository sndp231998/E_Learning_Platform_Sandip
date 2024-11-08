package com.e_learning.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.e_learning.entities.Carajol;
import com.e_learning.payloads.PostDto;
import com.e_learning.repositories.CarajolRepo;
import com.e_learning.services.FileService;

@RestController
@RequestMapping("/api/v1/carajol")
public class CarajolController {

	@Autowired
	private CarajolRepo carajolRepo;
	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
	
	 @GetMapping("/all")
	    public ResponseEntity<List<Carajol>> getAllCarajols() {
	        List<Carajol> carajols = carajolRepo.findAll();
	        if (carajols.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        }
	        return new ResponseEntity<>(carajols, HttpStatus.OK);
	    }
	
	
	@PostMapping("/upload")
    public ResponseEntity<Carajol> uploadCarajolImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

        // Allow only specific file types
        if (!fileExtension.equals("jpeg") && !fileExtension.equals("jpg") && !fileExtension.equals("png") && !fileExtension.equals("pdf") && !fileExtension.equals("pptx")) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        // Upload file and get the file name
        String fileName = fileService.uploadFile(path, file);

        // Create and save new Carajol entry
        Carajol carajol = new Carajol();
        carajol.setImageName(fileName);
        Carajol savedCarajol = carajolRepo.save(carajol);

        return new ResponseEntity<>(savedCarajol, HttpStatus.CREATED);
    }

    // Method to serve files based on file type
    @GetMapping("/image/{fileName}")
    public void downloadFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();
        MediaType mediaType;

        switch (fileExtension) {
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "jpg":
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case "pptx":
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        response.setContentType(mediaType.toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (InputStream resource = fileService.getResource(path, fileName)) {
            StreamUtils.copy(resource, response.getOutputStream());
        }
    }
}
	


