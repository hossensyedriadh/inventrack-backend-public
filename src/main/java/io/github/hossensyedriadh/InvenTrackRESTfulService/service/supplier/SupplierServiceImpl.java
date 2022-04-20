package io.github.hossensyedriadh.InvenTrackRESTfulService.service.supplier;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Supplier;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.SupplierToSupplierModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SupplierModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SupplierRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class SupplierServiceImpl implements SupplierService {
    private final ObjectFactory<SupplierRepository> supplierRepositoryObjectFactory;
    private final CurrentAuthenticationContext authenticationContext;
    private final SupplierToSupplierModel toModel;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public SupplierServiceImpl(ObjectFactory<SupplierRepository> supplierRepositoryObjectFactory,
                               CurrentAuthenticationContext authenticationContext,
                               SupplierToSupplierModel toModel,
                               HttpServletRequest httpServletRequest) {
        this.supplierRepositoryObjectFactory = supplierRepositoryObjectFactory;
        this.authenticationContext = authenticationContext;
        this.toModel = toModel;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<SupplierModel> getSuppliers(Pageable pageable) {
        Page<Supplier> supplierPage = supplierRepositoryObjectFactory.getObject().findAll(pageable);

        return supplierPage.map(toModel::convert);
    }

    @Override
    public List<SupplierModel> getSuppliers() {
        return supplierRepositoryObjectFactory.getObject().findAll().stream().map(toModel::convert).collect(Collectors.toList());
    }

    @Override
    public Optional<SupplierModel> getSupplier(String phone) {
        if (supplierRepositoryObjectFactory.getObject().findById(phone).isPresent()) {
            Supplier supplier = supplierRepositoryObjectFactory.getObject().findById(phone).get();
            return Optional.ofNullable(toModel.convert(supplier));
        }

        return Optional.empty();
    }

    @Override
    public Optional<SupplierModel> addSupplier(SupplierModel supplierModel) {
        if (supplierRepositoryObjectFactory.getObject().findById(supplierModel.getPhone()).isEmpty()) {
            Supplier supplier = new Supplier();
            supplier.setName(supplierModel.getName());
            supplier.setPhoneNo(supplierModel.getPhone());
            supplier.setEmail((supplierModel.getEmail() != null) ? supplierModel.getEmail() : null);
            supplier.setAddress(supplierModel.getAddress());
            supplier.setWebsite((supplierModel.getWebsite() != null) ? supplierModel.getWebsite() : null);
            supplier.setNotes((supplierModel.getNotes() != null) ? supplierModel.getNotes() : null);
            supplier.setAddedBy(this.authenticationContext.getAuthenticatedUser());

            Supplier addedSupplier = supplierRepositoryObjectFactory.getObject().saveAndFlush(supplier);

            return Optional.ofNullable(toModel.convert(addedSupplier));
        }

        throw new ResourceCrudException("Supplier already exists with this phone number: " + supplierModel.getPhone(),
                HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    @Override
    public Optional<SupplierModel> updateSupplier(SupplierModel supplierModel) {
        if (supplierRepositoryObjectFactory.getObject().findById(supplierModel.getPhone()).isPresent()) {
            Supplier supplier = supplierRepositoryObjectFactory.getObject().getById(supplierModel.getPhone());
            supplier.setEmail((supplierModel.getEmail() != null) ? supplierModel.getEmail() : null);
            supplier.setAddress((supplierModel.getAddress() != null) ? supplierModel.getAddress() : supplier.getAddress());
            supplier.setWebsite((supplierModel.getWebsite() != null) ? supplierModel.getWebsite() : null);
            supplier.setNotes((supplierModel.getNotes() != null) ? supplierModel.getNotes() : null);
            supplier.setUpdatedBy(this.authenticationContext.getAuthenticatedUser());
            supplierRepositoryObjectFactory.getObject().saveAndFlush(supplier);

            Supplier updatedSupplier = supplierRepositoryObjectFactory.getObject().getById(supplier.getPhoneNo());

            return Optional.ofNullable(this.toModel.convert(updatedSupplier));
        }

        return Optional.empty();
    }
}
