package io.github.hossensyedriadh.inventrackrestfulservice.configuration.datasource;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.io.Serial;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@SuppressWarnings("all")
public class PostgreSQLEnumType extends EnumType {
    @Serial
    private static final long serialVersionUID = -5593061910523937371L;

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.toString(), Types.OTHER);
        }
    }
}
