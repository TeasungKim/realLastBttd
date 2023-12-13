package com.finalproject.bttd.apicontroller;

import com.finalproject.bttd.apiresponse.ApiResponse;
import com.finalproject.bttd.apiresponse.CustomException;
import com.finalproject.bttd.dto.*;
import com.finalproject.bttd.entity.Board;
import com.finalproject.bttd.entity.Comment;
import com.finalproject.bttd.entity.User;
import com.finalproject.bttd.repository.BoardRepository;
import com.finalproject.bttd.repository.CommentRepository;
import com.finalproject.bttd.repository.UserRepository;
import com.finalproject.bttd.security.CustomUserDetailService;
import com.finalproject.bttd.security.JWTGenerator;
import com.finalproject.bttd.service.BoardService;
import com.finalproject.bttd.service.CommentService;
import com.finalproject.bttd.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static com.finalproject.bttd.apiresponse.ApiResponse.ERROR_STATUS;
import static com.finalproject.bttd.apiresponse.ApiResponse.SUCCESS_STATUS;


@Slf4j
@RestController
public class ApiUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardRepository boardRepository;



    @PostMapping("/api/user")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody UserDto userDto) {
    try{
        log.info("UserDto Id : " + userDto.getUser_id());
        User created = userService.create(userDto);
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(SUCCESS_STATUS);
        response.setMessage("Success");
        response.setData(null);

        return ResponseEntity.ok(response);  }
        catch(Exception ex){
            ex.printStackTrace();
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(ERROR_STATUS); // ERROR_STATUS는 오류 상태를 나타내는 상수여야 함
            errorResponse.setMessage("중복된 아이디 값");
            errorResponse.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginDto loginDto){
        log.info("1 : "+ loginDto.getUser_id());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginDto.getUser_id(), loginDto.getUser_password()));
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
     //   SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenDto token = jwtGenerator.generateToken(authentication);
       // TokenDto tokenDto = jwtGenerator.generateToken(authentication);

        AuthResponseDto authResponseDto = new AuthResponseDto(token.getAccessToken(), token.getRefreshToken());

        ApiResponse<AuthResponseDto> response = new ApiResponse<>();
        response.setStatus(SUCCESS_STATUS);
        response.setMessage("Success");
        response.setData(authResponseDto);

        return ResponseEntity.ok(response);
    }

 @GetMapping("/api/reissue")
    public ResponseEntity<ApiResponse<AuthResponseDto>> reIssue(Principal principal){
     log.info("reissue first 1 : "+ principal.getName());
      String user_name = principal.getName();

        userRepository.findByuser_name(user_name);
        if (user_name != null){
            UserDetails userDetails = customUserDetailService.loadUserByUsername(user_name);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            TokenDto token = jwtGenerator.generateToken(authenticationToken);

            AuthResponseDto authResponseDto = new AuthResponseDto(token.getAccessToken(), token.getRefreshToken());

            ApiResponse<AuthResponseDto> response = new ApiResponse<>();
            response.setStatus(SUCCESS_STATUS);
            response.setMessage("Success");
            response.setData(authResponseDto);

            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AuthResponseDto> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(ERROR_STATUS);
            errorResponse.setMessage("User not found");
            errorResponse.setData(null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
 }

// 매칭글을 올리면 matching_post db생성 ,

    @PostMapping("/api/boardwrite")
    public ResponseEntity<ApiResponse<String>> boardwrite(@RequestBody BoardDto boardDto, Principal principal){


        String username = principal.getName();
        Optional<User> userOptional = userRepository.findByuser_name(username);

        if (!userOptional.isPresent()) {

            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setStatus(ERROR_STATUS);
            errorResponse.setMessage("User not found");
            errorResponse.setData(null);

            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        User user = userOptional.get();
        boardDto.setUser_id(user); // BoardDto에 User 객체를 직접 설정
        Board created = boardService.boardwrite(boardDto);

        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(SUCCESS_STATUS);
        response.setMessage("Success");
        response.setData(null);

        return ResponseEntity.ok(response);
    }

// 댓글이 달리면 matching_post의 post id 값을 가져와서 request_form 생성
    @PostMapping("/api/comment")
    public ResponseEntity<ApiResponse<String>> comments(@RequestBody CommentDto commentDto){
        log.info("commentDto : "+ commentDto);
        log.info("post id : " + commentDto.getPost_id());
        Comment created = commentService.comments(commentDto);

        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(SUCCESS_STATUS);
        response.setMessage("Success");
        response.setData(null);

        return ResponseEntity.ok(response);

    }

// matching_post의 유저가 컨펌을 하면 request_form의 유저아이디 mathicng_post의 away_id에 저장

    @PostMapping("/api/commentConfirm")
    public ResponseEntity<ApiResponse<String>> commentConfirm(@RequestBody CommentConfirmDto commentConfirmDto){
        int postId = commentConfirmDto.getPost_id();
        String userId = commentConfirmDto.getRequest_user_id();

        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        log.info("is this real board? :" + board);
        board.setAway_id(userId);
        boardRepository.save(board);

        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(SUCCESS_STATUS);
        response.setMessage("Success");
        response.setData(null);

        return ResponseEntity.ok(response);

    }

// 이후 승패가 났을때 score에 점수저장 해당 점수 user에 user_win, user_lose 디비저장



    @PostMapping("/api/score")
    @Transactional
public ResponseEntity<ApiResponse<String>> score(@RequestBody ScoreDto scoreDto){
        int post_id = scoreDto.getPost_id();
        boolean score = scoreDto.isScore();

        Board board = boardRepository.findById(post_id).orElse(null); //게시글 번호 가져와서 조회
        String home_id = board.getUser_id().getUser_id(); // 홈아이디 가져옴
        String away_id = board.getAway_id(); // 어웨이 아이디 가져옴
        User user = userRepository.findByuser_id(home_id).orElse(null);  //유저겍체를 홈아이디로 찾아옴
        User awayUser = userRepository.findByuser_id(away_id).orElse(null); // 유저객체를 어웨이아이디로 찾아옴
        if(score == false){
            //boardDto 의 user_id 를 찾아서 해당 user_id의 user_lose 1증가
            user.setUser_lose(user.getUser_lose() + 1);
            awayUser.setUser_win(awayUser.getUser_win() + 1);
            //boardDto 의 away_id 를 찾아서 해당 away_id의 user_win 1증가
        } else {
            //boardDto 의 user_id 를 찾아서 해당 user_id의 user_win 1증가
            user.setUser_win(user.getUser_win() + 1);
            awayUser.setUser_lose(user.getUser_lose() + 1);
            //boardDto 의 away_id 를 찾아서 해당 away_id의 user_lose 1증가
        }
            //이후 가져온 값들 각각 user의 win lose에 추가

        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(SUCCESS_STATUS);
        response.setMessage("Success");
        response.setData(null);


        return ResponseEntity.ok(response);
}

        @GetMapping("/api/getAllBoard")
        public ResponseEntity<ApiResponse<ArrayList<Board>>> getAllBaord(){

        ArrayList<Board> board = boardRepository.findAll();



            ApiResponse<ArrayList<Board>> response = new ApiResponse<>();
            response.setStatus(SUCCESS_STATUS);
            response.setMessage("Success");
            response.setData(board);
        return ResponseEntity.ok(response);
        }


        @GetMapping("/api/getAllComment")
        public ResponseEntity<ApiResponse<ArrayList<Comment>>> getAllComment(){

        ArrayList<Comment> comment = commentRepository.findAll();

            ApiResponse<ArrayList<Comment>> response = new ApiResponse<>();
            response.setStatus(SUCCESS_STATUS);
            response.setMessage("Success");
            response.setData(comment);
            return ResponseEntity.ok(response);
        }


//
}
