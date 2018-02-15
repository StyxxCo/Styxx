
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String>
{
	Account findByUsername(String username);
	Account findByAccountID(String accountID);
}
