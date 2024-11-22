package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;

	public FavoriteService(FavoriteRepository favoriteRepository, 
										HouseRepository houseRepository,
										UserRepository userRepository) {
		this.favoriteRepository = favoriteRepository;
		this.houseRepository = houseRepository;
		this.userRepository = userRepository;
	}

	/* お気に入り追加機能*/
	@Transactional
	public void add(Integer houseId, Integer userId) {
		Favorite favorite = new Favorite();
		User user = userRepository.getReferenceById(userId);
		House house = houseRepository.getReferenceById(houseId);
		favorite.setUser(user);
		favorite.setHouse(house);
		favoriteRepository.save(favorite);
	}

	/*お気に入り削除機能*/
	@Transactional
	public void delete(Integer houseId, Integer userId) {
		List<Favorite> favorites = favoriteRepository. findByHouseIdAndUserId(houseId,userId);
		if (favorites != null && !favorites.isEmpty()) {
			favoriteRepository.delete(favorites.get(0));
		}
	}
}
