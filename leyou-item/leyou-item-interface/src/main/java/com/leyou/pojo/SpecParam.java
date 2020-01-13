package com.leyou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Table(name ="tb_spec_param")
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    @Column(name = "`numeric`")
    private Boolean numeric;
    //单位
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
