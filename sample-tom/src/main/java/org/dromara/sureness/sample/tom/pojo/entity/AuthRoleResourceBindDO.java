package org.dromara.sureness.sample.tom.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * resource-role mapping entity
 * @author tomsun28
 * @date 00:28 2019-07-27
 */
@Entity
@Table(name = "auth_role_resource_bind")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRoleResourceBindDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "roleId can not null")
    private Long roleId;

    @NotNull(message = "resourceId can not null")
    private Long resourceId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtUpdate;
}
