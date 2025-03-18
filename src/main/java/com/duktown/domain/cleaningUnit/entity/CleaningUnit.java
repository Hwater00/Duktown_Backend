package com.duktown.domain.cleaningUnit.entity;

import com.duktown.domain.cleaning.entity.Cleaning;
import com.duktown.domain.unitUser.entity.UnitUser;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class CleaningUnit {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cleaning_unit_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cleaning_id", nullable = false)
    private Cleaning cleaning;// 청소

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "unit_user_id", nullable = false)
    private UnitUser unitUser;// 유닛 그룹 12명
}
