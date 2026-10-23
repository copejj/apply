package com.braindribbler.apply.controllers.companies;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.braindribbler.apply.dto.companies.CompanyDTO;
import com.braindribbler.apply.forms.companies.CompanyForm;
import com.braindribbler.apply.models.common.AddressType;
import com.braindribbler.apply.repositories.common.AddressTypeRepository;
import com.braindribbler.apply.repositories.common.StateRepository;
import com.braindribbler.apply.security.UserDetailsImpl;
import com.braindribbler.apply.service.companies.CompanyService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final StateRepository stateRepository;
    private final AddressTypeRepository addressTypeRepository;

    public CompanyController(CompanyService companyService, StateRepository stateRepository,
            AddressTypeRepository addressTypeRepository) {
        this.companyService = companyService;
        this.stateRepository = stateRepository;
        this.addressTypeRepository = addressTypeRepository;
    }

    @GetMapping
    public String getCompaniesByCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("companies", companyService.getAll(userDetails.getUserId()));

        model.addAttribute("location", "Companies");
        model.addAttribute("title", "Companies List");

        model.addAttribute("view", "companies/list :: companyTable(companies=${companies})");
        return "companies/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        CompanyForm form = new CompanyForm();

        model.addAttribute("location", "New Company");
        model.addAttribute("title", "Company Information");

        populateReferenceData(model); // Helper to keep code clean
        model.addAttribute("company", form);
        return "companies/edit"; // Reuse the same template
    }

    @PostMapping("/save")
    public String saveCompany(@Valid @ModelAttribute("company") CompanyForm companyForm,
            BindingResult result,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        cleanAddressList(companyForm); // Move your address cleanup to a private helper

        if (result.hasErrors()) {
            model.addAttribute("title", "Company Information");
            model.addAttribute("location", companyForm.getCompanyId() == null ? "New Company" : "Edit Company");
            populateReferenceData(model);
            return "companies/edit";
        }

        if (companyForm.getCompanyId() == null) {
            Long newId = companyService.createCompany(companyForm, userDetails.getUserId());
            redirectAttributes.addFlashAttribute("saveSuccess", "Company created successfully!");
            return "redirect:/companies/edit/" + newId + "?success=created";
        } else {
            companyService.updateCompany(companyForm);
            redirectAttributes.addFlashAttribute("saveSuccess", "Company updated successfully!");
            return "redirect:/companies/edit/" + companyForm.getCompanyId() + "?success=updated";
        }
    }

    private void populateReferenceData(Model model) {
        model.addAttribute("allStates", stateRepository.findAll());

        // Declaring the parameter type explicitly inside the lambda definition
        Sort modernSort = Sort.by(Sort.Direction.ASC, (AddressType type) -> type.getName());

        model.addAttribute("allAddressTypes", addressTypeRepository.findAll(modernSort));
    }

    // Helper for cleaning up empty rows
    private void cleanAddressList(CompanyForm form) {
        if (form.getAddresses() != null) {
            form.getAddresses().removeIf(addr -> (addr.getStreet() == null || addr.getStreet().isBlank()) &&
                    (addr.getCity() == null || addr.getCity().isBlank()));
        }
    }

    @GetMapping("/edit/{companyId}")
    public String showEditForm(@PathVariable Long companyId, Model model) {
        CompanyDTO dto = companyService.getCompanyDTOById(companyId);
        // Convert to writable form
        CompanyForm form = companyService.convertToForm(dto);

        model.addAttribute("location", "Edit Company");
        model.addAttribute("title", "Company Information");

        model.addAttribute("allStates", stateRepository.findAll());

        Sort modernSort = Sort.by(Sort.Direction.ASC, (AddressType type) -> type.getName());
        model.addAttribute("allAddressTypes", addressTypeRepository.findAll(modernSort));
        model.addAttribute("company", form); // 'company' matches th:object in your HTML
        return "companies/edit";
    }

    @PostMapping("/update")
    public String updateCompany(@Valid @ModelAttribute("company") CompanyForm companyForm,
            BindingResult result,
            Model model) {
        companyForm.getAddresses().removeIf(addr -> (addr.getStreet() == null || addr.getStreet().isBlank()) &&
                (addr.getCity() == null || addr.getCity().isBlank()));

        if (result.hasErrors()) {
            model.addAttribute("location", "Edit Company");
            model.addAttribute("title", "Fix Errors");
            return "companies/edit";
        }

        companyService.updateCompany(companyForm);
        return "redirect:/companies/edit/" + companyForm.getCompanyId();
    }
}
