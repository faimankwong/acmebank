package com.acmebank.accountmanager.repository;

import com.acmebank.accountmanager.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {}
