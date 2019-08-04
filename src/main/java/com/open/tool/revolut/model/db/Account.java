package com.open.tool.revolut.model.db;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "name"
                )
        }
)
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;


    private BigDecimal balance;

    @OneToMany(mappedBy = "to", fetch = FetchType.LAZY)
    private List<Operation> inOperations;

    @OneToMany(mappedBy = "from", fetch = FetchType.LAZY)
    private List<Operation> outOperations;


    public List<Operation> getInOperations() {
        return inOperations;
    }

    public List<Operation> getOutOperations() {
        return outOperations;
    }
}
