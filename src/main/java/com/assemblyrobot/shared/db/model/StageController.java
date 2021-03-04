package com.assemblyrobot.shared.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stage_controllers")
@NoArgsConstructor
public class StageController {
  @Id
  @Getter
  @Setter
  @Column(nullable = false)
  @GeneratedValue
  private long id;

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(nullable = false, name = "run_id")
  private Run run;

  @Getter
  @Setter
  @Column(nullable = false, name = "total_entered_material_amount")
  private double totalEnteredMaterialAmount;

  @Getter
  @Setter
  @Column(nullable = false, name = "total_exited_material_amount")
  private double totalExitedMaterialAmount;

  public StageController(double totalEnteredMaterialAmount, double totalExitedMaterialAmount) {
    this.totalEnteredMaterialAmount = totalEnteredMaterialAmount;
    this.totalExitedMaterialAmount = totalExitedMaterialAmount;
  }
}
