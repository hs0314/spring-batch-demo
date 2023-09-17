package me.heesu.springbatchdemo.core.domain.accounts;

import me.heesu.springbatchdemo.core.domain.orders.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
}
