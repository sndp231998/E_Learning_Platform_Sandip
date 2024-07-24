package com.e_learning.Controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.e_learning.payloads.AnswerDto;
import com.e_learning.payloads.ApiResponse;

import com.e_learning.services.AnswerService;
import com.e_learning.services.FileService;


@RestController
@RequestMapping("/api/v1/")
public class AnswerController {
	@Autowired
	private AnswerService answerService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
	@PostMapping("/answer/{answerId}/answers")
	public ResponseEntity<AnswerDto> createAnswer(@RequestBody AnswerDto answer, @PathVariable Integer examId) {

		AnswerDto createAnswer = this.answerService.createAnswer(answer, examId);
		return new ResponseEntity<AnswerDto>(createAnswer, HttpStatus.CREATED);
	}

	@DeleteMapping("/answers/{answerId}")
	public ResponseEntity<ApiResponse> deleteAnswer(@PathVariable Integer answerId) {

		this.answerService.deleteAnswer(answerId);

		return new ResponseEntity<ApiResponse>(new ApiResponse("Answer deleted successfully !!", true), HttpStatus.OK);
	}
	
	@PutMapping("/answers/{answerId}")
	public ResponseEntity<AnswerDto> updateAnswer(@RequestBody AnswerDto answerDto, @PathVariable Integer answerId) {

		AnswerDto updateAnswer = this.answerService.updateAnswer(answerDto, answerId);
		return new ResponseEntity<AnswerDto>(updateAnswer, HttpStatus.OK);

	}

	@GetMapping("/answers/{answerId}")
	public ResponseEntity<AnswerDto> getAnswerById(@PathVariable Integer answerId) {

		AnswerDto answerDto = this.answerService.getAnswerById(answerId);
		return new ResponseEntity<AnswerDto>(answerDto, HttpStatus.OK);

	}
	
	// Post method for file upload
    @PostMapping("/answer/file/upload/{answerId}")
    public ResponseEntity<AnswerDto> uploadAnswerFile(@RequestParam("file") MultipartFile file,
                                                  @PathVariable Integer answerId) throws IOException {
        AnswerDto answerDto = this.answerService.getAnswerById(answerId);
        String fileName = this.fileService.uploadFile(path, file);
        answerDto.setImageName(fileName);  // Assuming you want to set the uploaded file name as imageName
        AnswerDto updatedAnswer = this.answerService.updateAnswer(answerDto, answerId);
        return new ResponseEntity<>(updatedAnswer, HttpStatus.OK);
    }
    
 // Method to serve files
    @GetMapping(value = "/answer/file/{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public void downloadFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        InputStream resource = this.fileService.getResource(path, fileName);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if ("jpg".equals(fileExtension) || "jpeg".equals(fileExtension) || "png".equals(fileExtension)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        } else if ("pdf".equals(fileExtension)) {
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        } else if ("pptx".equals(fileExtension)) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }

        StreamUtils.copy(resource, response.getOutputStream());
    }

}
