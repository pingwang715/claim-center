package com.wangping.ClaimCenter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdjusterDto extends UserDto {
    private Long adjusterId;
}
