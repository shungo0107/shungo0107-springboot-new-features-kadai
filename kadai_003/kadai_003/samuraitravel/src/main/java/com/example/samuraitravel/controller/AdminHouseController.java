package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.service.HouseService;
 
 @Controller
 @RequestMapping("/admin/houses")
 /* @RequestMapping("/admin/houses")と設定することで、各メソッドに共通のパスを設定する必要がなくなる。
  *  indexメソッドの@GetMappingアノテーションにはマッピングするルートパスを指定していませんが、
  *  この場合は「/admin/houses」がそのままマッピングされます。
  */
 
 /*コンストラクタインジェクション
  * 　⇒あるクラス（A）が他のクラス（B）のオブジェクトを利用しているとき、Aの中でBのオブジェクトを
  * 　　直接生成するのではなく、Aに対してBの(実体化済みの)オブジェクトを外部から提供することを
  * 　　依存性の注入（Dependency Injection、DI）という。コンストラクタインジェクションは、Spring Bootが
  * 　　推奨するDIの方法。
  * 　　コンストラクタインジェクションを使う場合、@Autowiredアノテーションをつける。ただし、コンストラクタが
  * 　　一つの場合は省略可能。
  */
 // ↓↓
 public class AdminHouseController {
	    private final HouseRepository houseRepository;   
	    private final HouseService houseService;    
	        
	    public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {
	        this.houseRepository = houseRepository; 
	        this.houseService = houseService;  
	    }	
 // ↑↑
     
     /* 民宿一覧ページ（管理者用）
      *  コントローラからビューにデータを渡す場合、Modelクラスを使います。
      *  使い方
      *  　①メソッドにModel型の引数を指定する
      *  　②メソッド内でaddAttribute()メソッドを使い、以下の引数を渡す。
      *  　　（第一引数：ビュー側から参照する変数名、第2引数：ビューに渡すデータ）
      */
     // 
     @GetMapping
     public String index(Model model, 
    		                       /* Pageable型の引数を指定することで、Spring Boot側で適切なPageableオブジェクトを生成し、
    		                        * メソッド内で利用できるようにしてくれる。@PageableDefaultでは、Pageableオブジェクトが持つ
    		                        * ページ情報（ページ番号・1ページ当たりの表示数・ソート条件）をデフォルト値を任意に設定できる
    		                        */
    		                      @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    		                      @RequestParam(name = "keyword", required = false) String keyword) {
         Page<House> housePage;
         
         if (keyword != null && !keyword.isEmpty()) {
             housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);                
         } else {
             housePage = houseRepository.findAll(pageable);
         }  
                 
         model.addAttribute("housePage", housePage);   
         model.addAttribute("keyword", keyword);
         
         return "admin/houses/index";
     } 
     
     // 民宿詳細ページ（管理者用）
     @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
         House house = houseRepository.getReferenceById(id);
         model.addAttribute("house", house);
         return "admin/houses/show";
     }  
     
     @GetMapping("/register")
     public String register(Model model) {
         model.addAttribute("houseRegisterForm", new HouseRegisterForm());
         return "admin/houses/register";
     } 

     @PostMapping("/create")
     /* BindingResultは、バリデーションの結果を保持するためのインターフェースです。メソッドに
      * BindingResult型の引数を設定することで、バリデーションのエラー内容がその引数に格納されます。
      * また、BindingResultインターフェースが提供するhasErrors()メソッドを使うことで、エラーが
      * 存在するかどうかをチェックすることができます。
      */
     public String create(@ModelAttribute 
    		                        @Validated HouseRegisterForm houseRegisterForm, 
    		                        BindingResult bindingResult,
    		                        RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
        	 // エラーがあった場合にリターンし、エラーが発生している箇所にエラーメッセージを表示する
             return "admin/houses/register";
         }
         
         houseService.create(houseRegisterForm);
         /* RedirectAttributesインターフェースが提供するaddFlashAttribute()メソッドを使うことで、
          * リダイレクト先にデータを渡すことができます。
          * （第1引数：リダイレクト先から参照する変数名、第2引数：リダイレクト先に渡すデータ）
          */
         redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");    
         
         // ビューを呼び出すのではなくリダイレクトさせたい場合、return "redirect:ルートパス"のように記述します。
         return "redirect:/admin/houses";
     }
     
     // 管理者用新規登録
     @GetMapping("/{id}/edit")
     public String edit(@PathVariable(name = "id") Integer id, Model model) {
    	 // リポジトリから該当するIDのデータを取得する
         House house = houseRepository.getReferenceById(id);
         String imageName = house.getImageName();
         // 既存の登録内容が含まれている状態でインスタンス化したフォームクラスをビューに渡す
         HouseEditForm houseEditForm = new HouseEditForm(house.getId(), house.getName(), null, house.getDescription(), house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(), house.getPhoneNumber());
         
         // 生成したインスタンスをビューに渡す。（ビュー側から参照する変数 , 渡すデータ）
         model.addAttribute("imageName", imageName);
         model.addAttribute("houseEditForm", houseEditForm);
         
         return "admin/houses/edit";
     }  
     
     // 更新（管理者用編集）
     @PostMapping("/{id}/update")
     public String update(@ModelAttribute 
    		                        @Validated HouseEditForm houseEditForm,
    		                        BindingResult bindingResult,
    		                        RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/houses/edit";
         }
         
         houseService.update(houseEditForm);
         redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");
         
         return "redirect:/admin/houses";
     }   

     // 削除（管理者用編集）
     @PostMapping("/{id}/delete")
     public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
         houseRepository.deleteById(id);
                 
         redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");
         
         return "redirect:/admin/houses";
     }    
}