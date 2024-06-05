package com.alya.poc.model;

import com.remiges.alya.annotation.FieldType;
import com.remiges.alya.annotation.ValidField;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alya_entity") // Specify the desired table name
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ValidField(message = "Invalid name", fieldType = FieldType.NAME)
    private String name;

    @ValidField(message = "Invalid email", fieldType = FieldType.EMAIL)
    private String email;

    @ValidField(message = "Invalid PAN format", fieldType = FieldType.PAN)
    private String pan;

    @ValidField(message = "Invalid Aadhar number format", fieldType = FieldType.AADHAR)
    private String aadhar;
}    

