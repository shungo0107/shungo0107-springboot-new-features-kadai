package com.example.samuraitravel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;

@Service
public class HouseService {
    private final HouseRepository houseRepository;    
    
    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;        
    }    
    
    @Transactional
    // 新規登録
    public void create(HouseRegisterForm houseRegisterForm) {
        House house = new House();        
        MultipartFile imageFile = houseRegisterForm.getImageFile();
        
        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            house.setImageName(hashedImageName);
        }
        
        house.setName(houseRegisterForm.getName());                
        house.setDescription(houseRegisterForm.getDescription());
        house.setPrice(houseRegisterForm.getPrice());
        house.setCapacity(houseRegisterForm.getCapacity());
        house.setPostalCode(houseRegisterForm.getPostalCode());
        house.setAddress(houseRegisterForm.getAddress());
        house.setPhoneNumber(houseRegisterForm.getPhoneNumber());
                    
        houseRepository.save(house);
    }  
    
    @Transactional
    /*
     *  更新。管理者用更新。
     *  create()メソッドとほとんど一緒ですが、update()メソッドではエンティティを
     *  インスタンス化するのではなく、idを使って更新するエンティティを取得します。
     */
    public void update(HouseEditForm houseEditForm) {
        House house = houseRepository.getReferenceById(houseEditForm.getId());
        MultipartFile imageFile = houseEditForm.getImageFile();
        
        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            house.setImageName(hashedImageName);
        }
        
        house.setName(houseEditForm.getName());                
        house.setDescription(houseEditForm.getDescription());
        house.setPrice(houseEditForm.getPrice());
        house.setCapacity(houseEditForm.getCapacity());
        house.setPostalCode(houseEditForm.getPostalCode());
        house.setAddress(houseEditForm.getAddress());
        house.setPhoneNumber(houseEditForm.getPhoneNumber());
                    
        houseRepository.save(house);
    }    
    
    // UUIDを使って生成したファイル名を返す
    public String generateNewFileName(String fileName) {
       // 元のファイル名を"."で区切り、ファイル名と拡張子に分ける
        String[] fileNames = fileName.split("\\.");
       // ファイル名の場所にランダム文字列を格納する
        for (int i = 0; i < fileNames.length - 1; i++) {
            fileNames[i] = UUID.randomUUID().toString();            
        }
        // ランダム文字列と拡張子を連結した文字列をリターンする        
        String hashedFileName = String.join(".", fileNames);
        return hashedFileName;
    }     
    
    // 画像ファイルを指定したファイルにコピーする
    public void copyImageFile(MultipartFile imageFile, Path filePath) {           
        try {
        	// ファイルの内容を読み取り、コピーする。MultipartFileインターフェースが提供するメソッド。
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }          
    } 
}
