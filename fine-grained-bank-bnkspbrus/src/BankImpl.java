import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank implementation.
 *
 * @author Barsukov Nikita
 */
public class BankImpl implements Bank {
    /**
     * An array of accounts by index.
     */
    private final Account[] accounts;

    /**
     * Creates new bank instance.
     *
     * @param n the number of accounts (numbered from 0 to n-1).
     */
    public BankImpl(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = new Account();
        }
    }

    @Override
    public int getNumberOfAccounts() {
        return accounts.length;
    }

    @Override
    public long getAmount(int index) {
        try {
            accounts[index].lock.lock();
            return accounts[index].amount;
        } finally {
            accounts[index].lock.unlock();
        }
    }

    @Override
    public long getTotalAmount() {
        long sum = 0;
        try {
            for (Account account : accounts) {
                account.lock.lock();
            }
            for (Account account : accounts) {
                sum += account.amount;
            }
            return sum;
        } finally {
            for (Account account : accounts) {
                account.lock.unlock();
            }
        }
    }

    @Override
    public long deposit(int index, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount: " + amount);
        Account account = accounts[index];
        try {
            account.lock.lock();
            if (amount > MAX_AMOUNT || account.amount + amount > MAX_AMOUNT)
                throw new IllegalStateException("Overflow");
            account.amount += amount;
            return account.amount;
        } finally {
            account.lock.unlock();
        }
    }

    /**
     *
     */
    @Override
    public long withdraw(int index, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount: " + amount);
        Account account = accounts[index];
        try {
            account.lock.lock();
            if (account.amount - amount < 0)
                throw new IllegalStateException("Underflow");
            account.amount -= amount;
            return account.amount;
        } finally {
            account.lock.unlock();
        }
    }

    @Override
    public void transfer(int fromIndex, int toIndex, long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount: " + amount);
        if (fromIndex == toIndex)
            throw new IllegalArgumentException("fromIndex == toIndex");
        Account from = accounts[fromIndex];
        Account to = accounts[toIndex];
        ReentrantLock lock1 = fromIndex < toIndex ? from.lock : to.lock;
        ReentrantLock lock2 = toIndex > fromIndex ? to.lock : from.lock;
        try {
            lock1.lock();
            lock2.lock();
            if (amount > from.amount)
                throw new IllegalStateException("Underflow");
            else if (amount > MAX_AMOUNT || to.amount + amount > MAX_AMOUNT)
                throw new IllegalStateException("Overflow");
            from.amount -= amount;
            to.amount += amount;
        } finally {
            lock1.unlock();
            lock2.unlock();
        }
    }

    /**
     * Private account data structure.
     */
    static class Account {
        public final ReentrantLock lock = new ReentrantLock();
        /**
         * Amount of funds in this account.
         */
        long amount;
    }
}
