package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.SignupInvitation;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.service.invitations.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasAuthority('ROLE_ROOT')")
@RestController
@RequestMapping(value = "/v1/invitations", produces = {MediaTypes.HAL_JSON_VALUE})
public class InvitationController {
    private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    private int defaultPageSize;

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/")
    public ResponseEntity<?> invitations(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<SignupInvitation> invitations = invitationService.invitations(pageable);

        if (invitations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(invitations);

        List<EntityModel<SignupInvitation>> invitationEntityModels = new ArrayList<>();

        for (int i = 0; i < invitations.getContent().size(); i += 1) {
            SignupInvitation invitation = invitationService.invitation(invitations.getContent().get(i).getId());
            EntityModel<SignupInvitation> invitationEntityModel = EntityModel.of(invitation);

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(invitation.getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get invitation")
                    .withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(new SignupInvitation()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Create invitation")
                    .withType(HttpMethod.POST.toString()));

            if (invitation.getStatus().equals(InvitationStatus.VALID)) {
                invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(invitation.getId()))
                        .withRel("invalidate").withTitle("Invalidate invitation")
                        .withType(HttpMethod.PATCH.toString()));
            }
            invitationEntityModels.add(invitationEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("invitations", invitationEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(page, size,
                        SignupInvitation.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (invitations.getTotalPages() - 1) && invitations.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(0, size,
                            SignupInvitation.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(page - 1, size,
                            SignupInvitation.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (invitations.getTotalPages() - 1) && invitations.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(page + 1, size,
                            SignupInvitation.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(invitations.getTotalPages() - 1, size,
                            SignupInvitation.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> invitation(@PathVariable("id") String id) {
        SignupInvitation invitation = invitationService.invitation(id);

        EntityModel<SignupInvitation> invitationEntityModel = EntityModel.of(invitation);

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(0, defaultPageSize,
                        SignupInvitation.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("invitations").withTitle("Get invitations").withType(HttpMethod.GET.toString()));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(invitation.getId()))
                .withRel(IanaLinkRelations.SELF).withTitle("Get invitation").withType(HttpMethod.GET.toString()));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(new SignupInvitation()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Create invitation").withType(HttpMethod.POST.toString()));

        if (invitation.getStatus().equals(InvitationStatus.VALID)) {
            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(invitation.getId()))
                    .withRel("invalidate").withTitle("Invalidate invitation").withType(HttpMethod.PATCH.toString()));
        }

        return new ResponseEntity<>(invitationEntityModel, HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> create(@Valid @RequestBody SignupInvitation signupInvitation) {
        SignupInvitation invitation = this.invitationService.create(signupInvitation);

        EntityModel<SignupInvitation> invitationEntityModel = EntityModel.of(invitation);

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(0, defaultPageSize,
                        SignupInvitation.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("invitations").withTitle("Get invitations").withType(HttpMethod.GET.toString()));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(invitation.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get invitation").withType(HttpMethod.GET.toString()));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(new SignupInvitation()))
                .withRel(IanaLinkRelations.SELF).withType(HttpMethod.POST.toString()).withTitle("Create invitation"));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(invitation.getId()))
                .withRel("invalidate").withType(HttpMethod.PATCH.toString()).withTitle("Invalidate invitation"));

        return new ResponseEntity<>(invitationEntityModel, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/invalidate/{id}")
    public ResponseEntity<?> invalidate(@PathVariable("id") String id) {
        SignupInvitation invitation = this.invitationService.invalidate(id);

        EntityModel<SignupInvitation> invitationEntityModel = EntityModel.of(invitation);

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(0, defaultPageSize,
                        SignupInvitation.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("invitations").withTitle("Get invitations").withType(HttpMethod.GET.toString()));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(invitation.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get invitation").withType(HttpMethod.GET.toString()));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(new SignupInvitation()))
                .withRel(IanaLinkRelations.CREATE_FORM).withType(HttpMethod.POST.toString()).withTitle("Create invitation"));

        invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(id))
                .withRel(IanaLinkRelations.SELF).withType(HttpMethod.PATCH.toString()).withTitle("Invalidate invitation"));

        return new ResponseEntity<>(invitationEntityModel, HttpStatus.OK);
    }
}
