package com.ogc_prototype.ogc.service.clientes;

import com.ogc_prototype.ogc.config.PasswordManager;
import com.ogc_prototype.ogc.dto.request.CustomerRequest;
import com.ogc_prototype.ogc.dto.response.CustomerResponse;
import com.ogc_prototype.ogc.exception.CustomerException;
import com.ogc_prototype.ogc.model.Customer;
import com.ogc_prototype.ogc.model.enums.Role;
import com.ogc_prototype.ogc.repository.CustomerRepository;
import com.ogc_prototype.ogc.repository.PasswordHistoryRepository;
import com.ogc_prototype.ogc.service.clientes.VerificationService;
import com.ogc_prototype.ogc.service.clientes.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordManager passwordManager;

    @Mock
    private VerificationService verificationService;

    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer buildCustomer(int id) {
        return Customer.builder().id(id).name("Bob").lastName("Brown").email("bob@example.com")
                .userName("bob99").password("$2a$10$hash").role(Role.CUSTOMER)
                .phoneNumber("+34611111111").address("Avenida 5").city("Barcelona").zipCode("08001")
                .country("ES").build();
    }

    private CustomerRequest buildRequest() {
        return CustomerRequest.builder().name("Bob").lastName("Brown").email("bob@example.com")
                .userName("bob99").password("passwordMustBe24CharsLong!!")
                .phoneNumber("+34611111111").address("Avenida 5").city("Barcelona").zipCode("08001")
                .country("ES").build();
    }

    @Test
    void getAll_returnsList() {
        when(customerRepository.findAll()).thenReturn(List.of(buildCustomer(1)));

        List<CustomerResponse> result = service.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_found_returnsResponse() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(buildCustomer(1)));

        CustomerResponse response = service.getById(1);

        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    void getById_notFound_throwsCustomerException() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99)).isInstanceOf(CustomerException.class);
    }

    @Test
    void create_allUnique_encodesPasswordAndSaves() {
        CustomerRequest req = buildRequest();
        when(customerRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(customerRepository.existsByUserName(req.getUserName())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(req.getPhoneNumber())).thenReturn(false);
        when(passwordManager.encode(req.getPassword())).thenReturn("$2a$10$hashedBob");
        when(customerRepository.save(any())).thenReturn(buildCustomer(1));

        CustomerResponse response = service.create(req);

        assertThat(response).isNotNull();
        verify(passwordManager).encode(req.getPassword());
        verify(customerRepository).save(any());
    }

    @Test
    void create_duplicateEmail_throwsCustomerException() {
        CustomerRequest req = buildRequest();
        when(customerRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(CustomerException.class);
    }

    @Test
    void create_duplicateUsername_throwsCustomerException() {
        CustomerRequest req = buildRequest();
        when(customerRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(customerRepository.existsByUserName(req.getUserName())).thenReturn(true);

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(CustomerException.class);
    }

    @Test
    void create_duplicatePhone_throwsCustomerException() {
        CustomerRequest req = buildRequest();
        when(customerRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(customerRepository.existsByUserName(req.getUserName())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(req.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(CustomerException.class);
    }

    @Test
    void update_existingCustomer_savesAndReturns() {
        Customer existing = buildCustomer(1);
        CustomerRequest req = buildRequest();
        when(customerRepository.findById(1)).thenReturn(Optional.of(existing));
        when(passwordManager.encode(any())).thenReturn("$2a$10$newHash");
        when(customerRepository.save(any())).thenReturn(existing);

        CustomerResponse response = service.update(1, req);

        assertThat(response).isNotNull();
        verify(customerRepository).save(any());
    }

    @Test
    void update_notFound_throwsCustomerException() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, buildRequest()))
                .isInstanceOf(CustomerException.class);
    }

    // ---- changeRole ----

    @Test
    void changeRole_notFound_throwsCustomerException() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeRole(99, Role.ADMIN))
                .isInstanceOf(CustomerException.class);
    }

    @Test
    void changeRole_sameRole_throwsCustomerException() {
        Customer customer = buildCustomer(1); // role = CUSTOMER
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> service.changeRole(1, Role.CUSTOMER))
                .isInstanceOf(CustomerException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    void changeRole_differentRole_updatesAndReturns() {
        Customer customer = buildCustomer(1); // role = CUSTOMER
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        CustomerResponse response = service.changeRole(1, Role.ADMIN);

        assertThat(customer.getRole()).isEqualTo(Role.ADMIN);
        assertThat(response).isNotNull();
        verify(customerRepository).save(customer);
    }
}
