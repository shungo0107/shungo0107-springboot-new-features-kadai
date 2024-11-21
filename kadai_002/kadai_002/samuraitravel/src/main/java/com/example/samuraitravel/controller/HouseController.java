package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;

@Controller
@RequestMapping("/houses")
// 民宿一覧ページを担当するコントローラ
public class HouseController {
    private final HouseRepository houseRepository;        
    private final ReviewRepository reviewRepository;            
    
    public HouseController(HouseRepository houseRepository,ReviewRepository reviewRepository) {
        this.houseRepository = houseRepository;
        this.reviewRepository = reviewRepository;        
    }     
  
    @GetMapping
    /* どのパラメータが存在するか（どの検索フォームが送信されたか）によって、民宿データの検索方法を
     * 条件分岐させる。これにより、ユーザーが送信した検索フォームの内容に応じて、適切な検索結果を
     * 表示することができます。
     */
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(name = "area", required = false) String area,
                                      @RequestParam(name = "price", required = false) Integer price, 
                                      @RequestParam(name = "order", required = false) String order,
                                      @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                                      Model model) 
    {
        Page<House> housePage;
                
        if (keyword != null && !keyword.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }            
       } else if (area != null && !area.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }            
       } else if (price != null) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }            
       } else {
            housePage = houseRepository.findAll(pageable);
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
            }            
       }                
        
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price);
        model.addAttribute("order", order);
        
        return "houses/index";
    }
    
    @GetMapping("/{id}") 
    public String show(@PathVariable(name = "id") Integer id,
    		                     @PageableDefault(page = 0, size = 6,direction = Direction.ASC) Pageable pageable,
    		                     Model model) {
        House house = houseRepository.getReferenceById(id);
        Page<Review> review = reviewRepository.findByHouseId(id,pageable);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean hasUserReviewed = review.stream().anyMatch(reviews -> reviews.getUser().getEmail().equals(authentication.getName()));

        model.addAttribute("house", house); 
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("review", review);
        model.addAttribute("hasUserReviewed", hasUserReviewed);
        model.addAttribute("loginuser",authentication.getName());
        
        return "houses/show";
    } 
    
}