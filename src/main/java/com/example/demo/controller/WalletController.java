package com.example.demo.controller;

import com.example.demo.domain.CoinGameResult;
import com.example.demo.domain.User;
import com.example.demo.domain.Wallet;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;

    public WalletController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getBalance(@RequestParam String chatId){
        Long parseChatId = Long.valueOf(chatId);
        User userByChatId = userService.getUserByChatId(parseChatId);
        Optional<Wallet> walletByUser = walletService.getWalletByUser(userByChatId);

        Map<String, Object> response = new HashMap<>();
        response.put("balance", walletByUser.get().getBalance());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> updateBalance(@RequestBody CoinGameResult coinGameResult){
        Long chatId = coinGameResult.getChatId();
        User userByChatId = userService.getUserByChatId(chatId);

        Wallet wallet = null;
        try {
            wallet = walletService.updateByUser(coinGameResult, userByChatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(wallet.getBalance());
    }
}
