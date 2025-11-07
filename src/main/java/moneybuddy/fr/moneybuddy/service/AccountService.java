package moneybuddy.fr.moneybuddy.service;

import org.apache.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.repository.AccountRepository;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.utils.Utils;
import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.model.Account;
import moneybuddy.fr.moneybuddy.model.enums.PlanType;
import moneybuddy.fr.moneybuddy.model.enums.Role;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SubAccountRepository subAccountRepository;
    private final Utils utils;

    public ResponseEntity<Page<Account>> getAccounts (
        PlanType planType,
        int page, 
        int size, 
        String sortBy, 
        String sortDir
    ) {
        Pageable pageable = utils.pagination(page, size, sortBy, sortDir);
        
        Page<Account> accounts = (planType != null)
        ? accountRepository.findAllByPlanType(planType, pageable)
        : accountRepository.findAll(pageable);
        
        return ResponseEntity.status(HttpStatus.SC_OK).body(accounts);
    }

    public ResponseEntity<Account> getAccount (String id) {
        Account account = accountRepository.findById(id).orElseThrow();
        return ResponseEntity.status(HttpStatus.SC_OK).body(account);
    }
    
    public ResponseEntity<ResponseDto> deleteAccount (String id) {
        accountRepository.deleteById(id);
        subAccountRepository.deleteAllByAccountId(id);
        return ResponseEntity.status(204).body(null); 
    }

    public ResponseEntity<ResponseDto> desableAccount (String id) {
        Account account = accountRepository.findById(id).orElseThrow();
        if (Role.ADMIN.equals(account.getRole())) {
            return ResponseEntity.status(403).body(null); 
        }
        
        account.setActivated(false);
        accountRepository.save(account);
        return ResponseEntity.status(204).body(null); 
    }
}
