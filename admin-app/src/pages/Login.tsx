import { Button, Card, Alert, Typography } from "antd";
import { LoginOutlined } from "@ant-design/icons";
import { useAuth } from "../auth/useAuth";

const { Title, Text } = Typography;

export default function Login() {
  const { login, authError, clearAuthError } = useAuth();

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <Card className="w-full max-w-sm shadow-md">
        <div className="text-center mb-6">
          <Title level={3} className="!mb-1">
            CMC Global CMS
          </Title>
          <Text type="secondary">Sign in to manage site content</Text>
        </div>

        {authError && (
          <Alert
            message="Authentication error"
            description={authError}
            type="error"
            showIcon
            closable
            onClose={clearAuthError}
            className="mb-4"
          />
        )}

        <Button type="primary" block size="large" icon={<LoginOutlined />} onClick={() => login()}>
          Sign in
        </Button>
      </Card>
    </div>
  );
}
