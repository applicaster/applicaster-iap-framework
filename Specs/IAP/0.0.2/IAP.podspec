Pod::Spec.new do |s|
  s.name         = 'IAP'
  s.version      = '0.0.2'
  s.summary      = 'In App Purchases framework'
  s.license      = 'MIT'
  s.homepage     = 'https://github.com/applicaster/applicaster-iap-framework'
  s.author       = { 'Roman Karpievich' => 'r.karpievich@applicaster.com' }
  s.ios.deployment_target = '9.0'
  s.swift_version = '4.2'
  s.source       = { :git => "https://github.com/applicaster/applicaster-iap-framework", :tag => s.version }
  s.source_files = 'iOS/IAP/*.{swift}'
  s.requires_arc = true
end