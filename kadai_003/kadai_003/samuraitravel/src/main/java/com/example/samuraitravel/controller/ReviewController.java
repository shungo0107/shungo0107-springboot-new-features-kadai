package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewInputForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/review")
public class ReviewController {
	 private final ReviewRepository reviewRepository;
	 private final ReviewService reviewService;
	 private final HouseRepository houseRepository;
	 
	    public ReviewController(ReviewRepository reviewRepository,
	    		                             HouseRepository houseRepository,
	    		                             ReviewService reviewService) {
	        this.reviewRepository = reviewRepository;
	        this.houseRepository = houseRepository;
	        this.reviewService = reviewService;
	    }    
	    
	    @GetMapping("/list/{id}")
	    public String index( @PathVariable(name = "id") Integer id,
	                                  @PageableDefault(page = 0, size = 6, direction = Direction.ASC) Pageable pageable,
	                                  @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	    		                      Model model) {

	    	House house = houseRepository.getReferenceById(id);
	    	Page<Review> review = reviewRepository. findByHouseId(id, pageable);

	        model.addAttribute("review",review);
	        model.addAttribute("house",house);  
	        
	        if (userDetailsImpl != null) {
	        	User user = userDetailsImpl.getUser();
		        model.addAttribute("user",user);
	        	
	        }
	        
	        return "review/list";
	    } 
	   
	    @GetMapping("/register/{id}")
	    public String register(@PathVariable(name = "id") Integer id,
	    		                         Model model) {
	    	
	    	House house = houseRepository.getReferenceById(id);
	    		    	
	    	 model.addAttribute("reviewInputForm",new ReviewInputForm());
	    	 model.addAttribute("house",house);  
	    	 
	    	return "review/register";
	    }
	    
	    @PostMapping("/create/{id}")	    
	     public String create(@ModelAttribute 
                                        @Validated ReviewInputForm reviewInputForm, 
                                        BindingResult bindingResult,
                       	    		    @PathVariable(name = "id") Integer houseId,
                       	    		    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {        
	    	 
	    	 if (bindingResult.hasErrors()) {
	    		 // エラーがあった場合にリターンし、エラーが発生している箇所にエラーメッセージを表示する
	    		 House house = houseRepository.getReferenceById(houseId);
	    		 model.addAttribute("house",house);  
	    		 return "review/register";
	    		 }
	         User user = userDetailsImpl.getUser(); 
	    	 ReviewRegisterForm reviewRegisterForm = new  ReviewRegisterForm(houseId,user.getId(),reviewInputForm.getScore(),reviewInputForm.getComment());
	    	 reviewService.create(reviewRegisterForm);
	    	 
	    	 return "redirect:/houses/" + houseId;
	    }
	    
	    @GetMapping("/edit/{id}")
	    public String edit(@PathVariable(name = "id") Integer id,
	    		                         Model model) {
	    	
	    	House house = houseRepository.getReferenceById(id);
	    	Review review = reviewRepository.getReferenceById(id);
	    	ReviewEditForm reviewEditForm = new ReviewEditForm(id,review.getAssessment(),review.getComment());
	    	
	    	 model.addAttribute("reviewEditForm",reviewEditForm);
	    	 model.addAttribute("house",house);  
	    	 
	    	return "review/edit";
	    }
	    
	    @PostMapping("/update/{id}")
	    public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm,
	    		                        BindingResult bindingResult,
	    		                        @PathVariable(name = "id") Integer id,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
	    	
	    	Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
	    	
	    	 if (bindingResult.hasErrors()) {
	    		 // エラーがあった場合にリターンし、エラーが発生している箇所にエラーメッセージを表示する
	    		 House house = houseRepository.getReferenceById(review.getHouse().getId());
	    		 model.addAttribute("house",house);  
	    		 return "review/edit";
	    		 }
	    	 
	    	reviewService.update(reviewEditForm);

	    	return "redirect:/houses/" + review.getHouse().getId();
	    }
	    
	    @PostMapping("/delete/{reviewId}")
	    public String delete(@PathVariable(name = "reviewId") Integer reviewId,
	    								RedirectAttributes redirectAttributes) {

	    	
	    	Review review = reviewRepository.getReferenceById(reviewId);
	    	Integer houseId = review.getHouse().getId();
	        reviewRepository.deleteById(reviewId);
	        
        	return "redirect:/houses/" + houseId ;
	        }    
}
