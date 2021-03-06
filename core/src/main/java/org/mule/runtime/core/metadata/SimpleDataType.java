/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.metadata;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.MimeType;

/**
 * A data type that simply wraps a Java type.
 * <p>
 * This type also allows a mime type and encoding to be associated with the Java type.
 *
 * @since 1.0
 */
public class SimpleDataType<T> implements DataType<T>
{
    private static final long serialVersionUID = -4590745924720880358L;

    protected final Class<T> type;
    protected final String mimeType;
    protected final String encoding;

    SimpleDataType(Class<T> type, String mimeType, String encoding)
    {
        this.type = type;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    @Override
    public Class<T> getType()
    {
        return type;
    }

    @Override
    public String getMimeType()
    {
        return mimeType;
    }

    @Override
    public String getEncoding()
    {
        return encoding;
    }

    @Override
    public boolean isCompatibleWith(DataType dataType)
    {
        if (this == dataType)
        {
            return true;
        }
        if (dataType == null)
        {
            return false;
        }

        SimpleDataType that = (SimpleDataType) dataType;

        //ANY_MIME_TYPE will match to a null or non-null value for MimeType        
        if ((this.getMimeType() == null && that.getMimeType() != null || that.getMimeType() == null && this.getMimeType() != null) && !MimeType.ANY.equals(this.mimeType) && !MimeType.ANY.equals(that.mimeType))
        {
            return false;
        }

        if (this.getMimeType() != null && !this.getMimeType().equals(that.getMimeType()) && !MimeType.ANY.equals(that.getMimeType()) && !MimeType.ANY.equals(this.getMimeType()))
        {
            return false;
        }

        if (!fromPrimitive(this.getType()).isAssignableFrom(fromPrimitive(that.getType())))
        {
            return false;
        }

        return true;
    }


    private Class<?> fromPrimitive(Class<?> type)
    {
        Class<?> primitiveWrapper = getPrimitiveWrapper(type);
        if (primitiveWrapper != null)
        {
            return primitiveWrapper;
        }
        else
        {
            return type;
        }
    }

    private Class<?> getPrimitiveWrapper(Class<?> primitiveType)
    {
        if (boolean.class.equals(primitiveType))
        {
            return Boolean.class;
        }
        else if (float.class.equals(primitiveType))
        {
            return Float.class;
        }
        else if (long.class.equals(primitiveType))
        {
            return Long.class;
        }
        else if (int.class.equals(primitiveType))
        {
            return Integer.class;
        }
        else if (short.class.equals(primitiveType))
        {
            return Short.class;
        }
        else if (byte.class.equals(primitiveType))
        {
            return Byte.class;
        }
        else if (double.class.equals(primitiveType))
        {
            return Double.class;
        }
        else if (char.class.equals(primitiveType))
        {
            return Character.class;
        }
        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        SimpleDataType that = (SimpleDataType) o;

        if (!type.equals(that.type))
        {
            return false;
        }

        //ANY_MIME_TYPE will match to a null or non-null value for MimeType
        if ((this.mimeType == null && that.mimeType != null || that.mimeType == null && this.mimeType != null) && !MimeType.ANY.equals(that.mimeType))
        {
            return false;
        }

        if (this.mimeType != null && !mimeType.equals(that.mimeType) && !MimeType.ANY.equals(that.mimeType))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        return result;
    }


    @Override
    public String toString()
    {
        return "SimpleDataType{" +
               "type=" + (type == null ? null : type.getName()) +
               ", mimeType='" + mimeType + '\'' +
               ", encoding='" + encoding + '\'' +
               '}';
    }
}
