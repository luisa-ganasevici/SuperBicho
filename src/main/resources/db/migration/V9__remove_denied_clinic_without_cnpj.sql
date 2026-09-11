DELETE FROM SB_TB_CLINIC
WHERE id = 2
  AND email = 'hospimais@gmail.com'
  AND clinic_status = 'DENIED'
  AND cnpj IS NULL;