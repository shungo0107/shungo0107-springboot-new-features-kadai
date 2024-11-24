package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {
	private FavoriteRepository favoriteRepository;
	private FavoriteService favoriteService;	
	
	public FavoriteController(FavoriteRepository favoriteRepository,
											FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("/list")
	public String list(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
								@PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable pageable,
								Model model){
		
		Page <Favorite> favorite = favoriteRepository.findByUserId(userDetailsImpl.getUser().getId(),pageable);
		
		model.addAttribute("favoritePage",favorite);
		
		return "/favorite/list";
	}
	
	/* お気に入り追加機能 */
	@PostMapping("/add/{houseId}")
	public String add(@PathVariable("houseId") Integer houseId,
								@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userDetailsImpl.getUser();
		favoriteService.add(houseId, user.getId());
		return "redirect:/houses/" + houseId;
	}

	/* お気に入り解除機能 */
	@PostMapping("/delete/{houseId}")
	public String delete(@PathVariable("houseId") Integer houseId,
									@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userDetailsImpl.getUser();
		favoriteService.delete(user.getId(),houseId);
		return "redirect:/houses/" + houseId;
	}

}
