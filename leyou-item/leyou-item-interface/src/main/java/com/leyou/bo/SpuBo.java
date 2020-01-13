package com.leyou.bo;

import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/*新建一个类，继承SPU，并且拓展cname和bname属性*/

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SpuBo extends Spu {
    private String bname;
    private String cname;
    private List<Sku> skus;
    private SpuDetail spuDetail;


}
