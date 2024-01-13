package io.github.onecx.permission.rs.external.v1.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.io.github.onecx.permission.rs.external.v1.model.ProblemDetailInvalidParamDTOV1;
import gen.io.github.onecx.permission.rs.external.v1.model.ProblemDetailParamDTOV1;
import gen.io.github.onecx.permission.rs.external.v1.model.ProblemDetailResponseDTOV1;

@Mapper
public abstract class ExceptionMapper {

    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    public abstract ProblemDetailResponseDTOV1 exception(String errorCode, String detail);

    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        var dto = exception(ErrorKeys.CONSTRAINT_VIOLATIONS.name(), ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    public abstract List<ProblemDetailInvalidParamDTOV1> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "name", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ProblemDetailInvalidParamDTOV1 createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }

    public enum ErrorKeys {
        CONSTRAINT_VIOLATIONS;
    }

    public List<ProblemDetailParamDTOV1> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new ProblemDetailParamDTOV1();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }
}
