package com.braindribbler.apply.repositories.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.apply.models.admin.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {

}
