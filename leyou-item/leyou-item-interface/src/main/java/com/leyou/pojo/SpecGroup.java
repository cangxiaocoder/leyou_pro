package com.leyou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Table(name ="tb_spec_group")
public class SpecGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private String name;
    @Transient
    private List<SpecParam> params;
}
