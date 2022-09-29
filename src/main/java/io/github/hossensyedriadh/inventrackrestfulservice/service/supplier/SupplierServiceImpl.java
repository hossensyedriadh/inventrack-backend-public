package io.github.hossensyedriadh.inventrackrestfulservice.service.supplier;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Supplier;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SupplierRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final CurrentAuthenticationContext authenticationContext;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository,
                               CurrentAuthenticationContext authenticationContext,
                               HttpServletRequest httpServletRequest) {
        this.supplierRepository = supplierRepository;
        this.authenticationContext = authenticationContext;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<Supplier> suppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    @Override
    public List<Supplier> suppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier supplier(String phone) {
        if (supplierRepository.findById(phone).isPresent()) {
            return supplierRepository.findById(phone).get();
        }

        throw new ResourceException("Supplier not found with phone: " + phone, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public Supplier add(Supplier supplier) {
        if (supplierRepository.findById(supplier.getPhoneNo()).isEmpty()) {
            supplier.setAddedBy(this.authenticationContext.getAuthenticatedUser());
            supplierRepository.saveAndFlush(supplier);
        }

        throw new ResourceException("Supplier already exists with this phone number: " + supplier.getPhoneNo(),
                HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public Supplier update(Supplier supplier) {
        if (supplierRepository.findById(supplier.getPhoneNo()).isPresent()) {
            supplier.setUpdatedBy(this.authenticationContext.getAuthenticatedUser());
            supplierRepository.saveAndFlush(supplier);
        }

        throw new ResourceException("Supplier not found with phone: " + supplier.getPhoneNo(), HttpStatus.BAD_REQUEST, httpServletRequest);
    }
}
