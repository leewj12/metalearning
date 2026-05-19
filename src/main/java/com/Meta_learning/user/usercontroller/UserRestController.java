package com.Meta_learning.user.usercontroller;


import com.Meta_learning.user.userdto.FindUserIdDTO;
import com.Meta_learning.user.userdto.UserIdDTO;
import com.Meta_learning.user.userservice.UserService;
import com.Meta_learning.utility.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;


    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }


    //아이디 찾기 메서드임
    @PostMapping("/find/userid")
    @ResponseBody
    public ResponseEntity<ResponseMessage> findUserId(@RequestBody FindUserIdDTO findUserIdDTO) {
        // 이름, 생년월일, 연락처를 통해 사용자 아이디 찾기
        UserIdDTO userId = userService.findUserByNameBirthAndPhone(findUserIdDTO);

        if (userId != null) {
            ResponseMessage responseMessage = new ResponseMessage("success", userId.getUseremail());
            return ResponseEntity.ok(responseMessage);
        } else {
            ResponseMessage responseMessage = new ResponseMessage("error", "입력한 정보에 해당하는 아이디가 없습니다.");
            return ResponseEntity.ok(responseMessage);
        }
    }

// 유저 삭제하는 메서드임
    @DeleteMapping("/admin/users/delete/{userId}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable("userId") Long userId) {
        try {
            // 유저 삭제 로직
            boolean isDeleted = userService.deleteUser(userId);

            if (isDeleted) {
                // 유저 삭제가 성공한 경우
                ResponseMessage responseMessage = new ResponseMessage("success", "유저가 삭제되었습니다.");
                return ResponseEntity.ok(responseMessage);
            } else {
                // 유저 삭제가 실패한 경우 (해당 아이디가 없을 경우)
                ResponseMessage responseMessage = new ResponseMessage("error", "입력한 정보에 해당하는 아이디가 없습니다.");
                return ResponseEntity.ok(responseMessage);
            }
        } catch (Exception e) {
            // 예외 처리: 삭제 작업 중 오류가 발생한 경우
            ResponseMessage responseMessage = new ResponseMessage("failure", "삭제 작업에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }




}
