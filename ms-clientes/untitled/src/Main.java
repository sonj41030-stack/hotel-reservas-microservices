//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    Page
    Page number
    of 1
    This preview may have altered the layout of this file. You can still download the original file.
            SELECT
    t.descripcion
            || ','
            || s.descripcion AS sistema_salud,
    COUNT(a.ate_id) AS "TOTAL ATENCIONES",
            'CON descuento' AS "CORRESPONDE DESCUENTO"
    FROM
    tipo_salud t
    LEFT JOIN salud s ON t.tipo_sal_id = s.tipo_sal_id
    LEFT JOIN paciente p ON s.sal_id = p.sal_id
    LEFT JOIN atencion a ON p.pac_run = a.pac_run
    AND to_char(a.fecha_atencion, 'MM/YY') = '10/26'
    GROUP BY
    t.descripcion
            || ','
            || s.descripcion
    HAVING
    COUNT(a.ate_id) > (
            SELECT
    AVG(COUNT(a.ate_id))
    FROM
    tipo_salud t
    LEFT JOIN salud s ON t.tipo_sal_id = s.tipo_sal_id
    LEFT JOIN paciente p ON s.sal_id = p.sal_id
    LEFT JOIN atencion a ON p.pac_run = a.pac_run
    WHERE
    to_char(a.fecha_atencion, 'MM/YY') = '10/26'
    GROUP BY
    t.descripcion
            || ','
            || s.descripcion
)
    UNION
            SELECT
    t.descripcion
            || ','
            || s.descripcion AS sistema_salud,
    COUNT(a.ate_id) AS "TOTAL ATENCIONES",
            'SIN descuento' AS "CORRESPONDE DESCUENTO"
    FROM
    tipo_salud t
    LEFT JOIN salud s ON t.tipo_sal_id = s.tipo_sal_id
    LEFT JOIN paciente p ON s.sal_id = p.sal_id
    LEFT JOIN atencion a ON p.pac_run = a.pac_run
    AND to_char(a.fecha_atencion, 'MM/YY') = '10/26'
    GROUP BY
    t.descripcion
            || ','
            || s.descripcion
    HAVING
    COUNT(a.ate_id) < (
            SELECT
    AVG(COUNT(a.ate_id))
    FROM
    tipo_salud t
    LEFT JOIN salud s ON t.tipo_sal_id = s.tipo_sal_id
    LEFT JOIN paciente p ON s.sal_id = p.sal_id
    LEFT JOIN atencion a ON p.pac_run = a.pac_run
    WHERE
    to_char(a.fecha_atencion, 'MM/YY') = '10/26'
    GROUP BY
    t.descripcion
            || ','
            || s.descripcion
)
    ORDER BY
    1 ASCx


}
