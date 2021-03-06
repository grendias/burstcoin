package brs.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import brs.Account;
import brs.Account.RewardRecipientAssignment;
import brs.db.BurstIterator;
import brs.db.BurstKey;
import brs.db.BurstKey.LongKeyFactory;
import brs.db.VersionedBatchEntityTable;
import brs.db.store.AccountStore;
import brs.db.store.AssetTransferStore;
import org.junit.Before;
import org.junit.Test;

public class AccountServiceImplTest {

  private AccountServiceImpl t;

  private AccountStore accountStoreMock;
  private VersionedBatchEntityTable<Account> accountTableMock;
  private LongKeyFactory<Account> accountBurstKeyFactoryMock;
  private AssetTransferStore assetTransferStoreMock;

  @Before
  public void setUp() {
    accountStoreMock = mock(AccountStore.class);
    accountTableMock = mock(VersionedBatchEntityTable.class);
    accountBurstKeyFactoryMock = mock(LongKeyFactory.class);
    assetTransferStoreMock = mock(AssetTransferStore.class);

    when(accountStoreMock.getAccountTable()).thenReturn(accountTableMock);
    when(accountStoreMock.getAccountKeyFactory()).thenReturn(accountBurstKeyFactoryMock);

    t = new AccountServiceImpl(accountStoreMock, assetTransferStoreMock);
  }

  @Test
  public void getAccount() {
    final long mockId = 123l;
    final BurstKey mockKey = mock(BurstKey.class);
    final Account mockResultAccount = mock(Account.class);

    when(accountBurstKeyFactoryMock.newKey(eq(mockId))).thenReturn(mockKey);
    when(accountTableMock.get(eq((mockKey)))).thenReturn(mockResultAccount);

    assertEquals(mockResultAccount, t.getAccount(mockId));
  }

  @Test
  public void getAccount_id0ReturnsNull() {
    assertNull(t.getAccount(0));
  }

  @Test
  public void getAccount_withHeight() {
    final long id = 123l;
    final int height = 2;
    final BurstKey mockKey = mock(BurstKey.class);
    final Account mockResultAccount = mock(Account.class);

    when(accountBurstKeyFactoryMock.newKey(eq(id))).thenReturn(mockKey);
    when(accountTableMock.get(eq(mockKey), eq(height))).thenReturn(mockResultAccount);

    assertEquals(mockResultAccount, t.getAccount(id, height));
  }

  @Test
  public void getAccount_withHeight_0returnsNull() {
    assertNull(t.getAccount(0, 2));
  }

  @Test
  public void getAccount_withPublicKey() {
    byte[] publicKey = new byte[1];
    publicKey[0] = (byte) 1;

    final BurstKey mockKey = mock(BurstKey.class);
    final Account mockAccount = mock(Account.class);

    when(accountBurstKeyFactoryMock.newKey(anyLong())).thenReturn(mockKey);
    when(accountTableMock.get(mockKey)).thenReturn(mockAccount);

    when(mockAccount.getPublicKey()).thenReturn(publicKey);

    assertEquals(mockAccount, t.getAccount(publicKey));
  }

  @Test
  public void getAccount_withoutPublicKey() {
    byte[] publicKey = new byte[1];
    publicKey[0] = (byte) 1;

    final BurstKey mockKey = mock(BurstKey.class);
    final Account mockAccount = mock(Account.class);

    when(accountBurstKeyFactoryMock.newKey(anyLong())).thenReturn(mockKey);
    when(accountTableMock.get(mockKey)).thenReturn(mockAccount);

    when(mockAccount.getPublicKey()).thenReturn(null);

    assertEquals(mockAccount, t.getAccount(publicKey));
  }

  @Test
  public void getAccount_withPublicKey_notFoundReturnsNull() {
    final byte[] publicKey = new byte[0];
    final BurstKey mockKey = mock(BurstKey.class);

    when(accountBurstKeyFactoryMock.newKey(anyLong())).thenReturn(mockKey);
    when(accountTableMock.get(mockKey)).thenReturn(null);

    assertNull(t.getAccount(publicKey));
  }

  @Test(expected = RuntimeException.class)
  public void getAccount_withPublicKey_duplicateKeyForAccount() {
    byte[] publicKey = new byte[1];
    publicKey[0] = (byte) 1;
    byte[] otherPublicKey = new byte[1];
    otherPublicKey[0] = (byte) 2;

    final BurstKey mockKey = mock(BurstKey.class);
    final Account mockAccount = mock(Account.class);

    when(accountBurstKeyFactoryMock.newKey(anyLong())).thenReturn(mockKey);
    when(accountTableMock.get(mockKey)).thenReturn(mockAccount);

    when(mockAccount.getPublicKey()).thenReturn(otherPublicKey);

    t.getAccount(publicKey);
  }

  @Test
  public void getAssetTransfers() {
    final long accountId = 123L;
    final int from = 2;
    final int to = 3;

    t.getAssetTransfers(accountId, from, to);

    verify(assetTransferStoreMock).getAccountAssetTransfers(eq(accountId), eq(from), eq(to));
  }

  @Test
  public void getAccountsWithRewardRecipient() {
    final Long recipientId = 123l;
    final BurstIterator<RewardRecipientAssignment> mockAccountsIterator = mock(BurstIterator.class);

    when(accountStoreMock.getAccountsWithRewardRecipient(eq(recipientId))).thenReturn(mockAccountsIterator);

    assertEquals(mockAccountsIterator, t.getAccountsWithRewardRecipient(recipientId));
  }

  @Test
  public void getId() {
    final byte[] publicKeyMock = new byte[1];
    publicKeyMock[0] = (byte) 1;
    assertEquals(-4227678059763665589L,  AccountServiceImpl.getId(publicKeyMock));
  }
}